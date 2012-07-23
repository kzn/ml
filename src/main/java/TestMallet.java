
import gnu.trove.list.array.TDoubleArrayList;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import ru.iitp.proling.ml.core.MalletInstance;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.mallet.InstanceListPersistentBuilder;
import ru.iitp.proling.ml.mallet.SVMBinaryClassifier;
import ru.iitp.proling.ml.mallet.SVMBinaryClassifierTrainer;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.BinaryClassifier;
import ru.iitp.proling.svm.ClassifierEval;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.LMS;
import ru.iitp.proling.svm.SVMBinarySolver;
import ru.iitp.proling.svm.WeightVectorLinear;
import ru.iitp.proling.svm.WeightVectorMallet;
import ru.iitp.proling.svm.kernel.Kernel;
import ru.iitp.proling.svm.kernel.LinearKernel;
import ru.iitp.proling.svm.kernel.PolyHashKernel;


import cc.mallet.classify.NaiveBayes;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Target2Label;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.SparseVector;


public class TestMallet {
	public static class ImportSVM{
		public static InstanceList load(String filename, Alphabet dataAlphabet, LabelAlphabet labelAlphabet){
			InstanceList il = new InstanceList(new Target2Label(dataAlphabet, labelAlphabet));
			

			
			System.out.println("Reading:" + filename);

			try{
				BufferedReader fp = new BufferedReader(new FileReader(filename));

				StreamTokenizer ft = new StreamTokenizer(fp);
				ft.commentChar('#');
				ft.ordinaryChar(':');
				ft.eolIsSignificant(true);
				
				int n = 0;



				while(true){
					n++;
					
					if(n % 10000 == 0){
						System.out.print(Integer.toString(n) + "..");
						System.out.flush();
					
					}
					
					int ttype = ft.nextToken(); // token type

					if(ttype == StreamTokenizer.TT_EOF | ttype == StreamTokenizer.TT_EOL)
						break;


					assert(ttype == StreamTokenizer.TT_NUMBER);
					double target = ft.nval;
					

					List<Integer> idxs = new ArrayList<Integer>();
					List<Double> vals = new ArrayList<Double>();

					while(true){
						ttype = ft.nextToken();
						if(ttype == StreamTokenizer.TT_EOF | ttype == StreamTokenizer.TT_EOL)
							break;



						int idx = 0;
						if(ttype == StreamTokenizer.TT_NUMBER)
							idx = (int)ft.nval;
						
						
						int idx_type = ttype;
						String field = null;
						if(ft.sval != null)
							field = ft.sval.intern();


						ttype = ft.nextToken();

						assert(ttype == ':');

						ttype = ft.nextToken();
						assert(ttype == StreamTokenizer.TT_NUMBER);

						if(idx_type == StreamTokenizer.TT_NUMBER){
							idxs.add(idx);
							vals.add(ft.nval);

							
						}else{
							char c = field.charAt(0);

							switch(c){
							case 'q':
								break;
							case 'c':
								break;
							}


						}



					}
					
					int[] indArray = new int[idxs.size()];
					double[] valArray = new double[idxs.size()];
					for(int i = 0; i != idxs.size(); i++){
						indArray[i] = idxs.get(i);
						valArray[i] = vals.get(i);
					}
					
					FeatureVector fv = new FeatureVector(dataAlphabet, indArray, valArray);
					Double tgt = target;
					Instance inst = new Instance(fv, tgt, null, null);

					
					il.addThruPipe(inst);
				}
				
								
				System.out.print(Integer.toString(n - 1) + ".Done!\n");
			}catch (IOException e) {
				e.printStackTrace();
			}


			
			
			return il;
		}
		
		
	}

	public static InstanceList readBinary(String filename, int n) throws IOException, ClassNotFoundException{
		// 1. read data/target alphabets {filename + ".alpha")
		// 2. read instances.
		
		InputStream isAlpha = new FileInputStream(filename + ".alpha");
		ObjectInputStream oisAlpha = new ObjectInputStream(isAlpha);
		Alphabet data = (Alphabet)oisAlpha.readObject();
		Alphabet targets = (Alphabet)oisAlpha.readObject();
		oisAlpha.close();
		isAlpha.close();
		
		
		
		InstanceList il = new InstanceList(new Noop(data, targets));
		InputStream is = new GZIPInputStream(new FileInputStream(filename));
		ObjectInputStream ois = new ObjectInputStream(is);
		int i = 0;
		try{
			while(n == 0 || i++ < n){
				Instance instance = (Instance)ois.readObject();
				il.addThruPipe(instance);
			}
		}catch(EOFException e){
		}
		ois.close();
		is.close();
		
		
		
		return il;
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if(args.length < 1)
			throw new IllegalArgumentException("Number of aguments must be greater than 0");
		
		//InstanceList train = readBinary(args[0], 1000);
		InstanceList train = InstanceListPersistentBuilder.read(args[0],500000);
		//InstanceListPersistentBuilder.writeOut(train, "mst.dat");
		
		// 1 test that dataset is binary
		/*for(Instance inst : train){
			SparseVector sv = (SparseVector)inst.getData();
			for(int i = 0; i != sv.numLocations(); i++)
				if(sv.valueAtLocation(i) != 1.0)
					throw new IllegalArgumentException("Dataset not binary!");
		}*/
		
		// 2 compute 'train' and test subsets
		List<ru.iitp.proling.ml.core.Instance> trainSet = new ArrayList<ru.iitp.proling.ml.core.Instance>();
		TDoubleArrayList trainSetTargets = new TDoubleArrayList();
		List<ru.iitp.proling.ml.core.Instance> testSet = new ArrayList<ru.iitp.proling.ml.core.Instance>();
		TDoubleArrayList testSetTargets = new TDoubleArrayList();
		
		int split = 50000;
		int dim = 0;
		
		for(int i = 0; i != split; i++){
			SparseVector sv = (SparseVector)train.get(i).getData();
			trainSet.add(new ru.iitp.proling.ml.core.MalletInstance(sv));
			Label t = (Label)train.get(i).getTarget();
			Integer target = (Integer)train.getTargetAlphabet().lookupObject(t.getIndex());
			trainSetTargets.add(target.intValue() == 1? 1.0 : -1.0);
			dim = Math.max(dim, sv.singleSize());
		}
		
		for(int i = split; i != train.size(); i++){
			SparseVector sv = (SparseVector)train.get(i).getData();
			testSet.add(new ru.iitp.proling.ml.core.MalletInstance(sv));
			Label t = (Label)train.get(i).getTarget();
			Integer target = (Integer)train.getTargetAlphabet().lookupObject(t.getIndex());
			testSetTargets.add(target.intValue() == 1? 1.0 : -1.0);
		}
		
		Kernel k = new PolyHashKernel(1, 1, dim + 1, 1000000);
		
		WeightVectorMallet wv = new WeightVectorMallet(trainSet, trainSetTargets.toArray(), k);
		DCDSolver.verbosity = 5;
		Scorer s = DCDSolver.solve(wv, 0.05, 0.05, 5000, 0.1, 5000000, 1);
		
		BinaryClassifier<Double> cl = new BinaryClassifier<Double>(s, 1.0, -1.0);
		
		/*int correct = 0;
		int positive = 0;
		int correctPositive = 0;
		for(int i = 0; i != testSet.size(); i++){
			double res = cl.classify(testSet.get(i));
			if(res == testSetTargets.get(i))
				correct++;
			
			if(testSetTargets.get(i) == 1.0)
				positive++;
			
			if(res == testSetTargets.get(i) && testSetTargets.get(i) == 1.0)
				correctPositive++;
		}*/
		ClassifierEval.evalBinaryClassifierMallet(testSet, testSetTargets.toArray(), cl);
		
		
		
		//System.out.printf("dataset size:%d, dim:%d%n", train.size(), train.getDataAlphabet().size());
		
		//SVMBinaryClassifierTrainer tr = new SVMBinaryClassifierTrainer(0.05, 0.05, 0.01, 500, 2000000, new LinearKernel());
		
		//SVMBinaryClassifier c = tr.train(train);
		
		
		
		/*Alphabet data = new Alphabet();
		data.lookupIndex(new Integer(0));
		LabelAlphabet labels = new LabelAlphabet();
		InstanceList train = ImportSVM.load(args[0], data, labels);
		BasicDataset trainSet = BasicDataset.readText(args[0]);*/

		//System.out.printf("Alphabet size after test set: %d%n", data.size());
		
		//double[] targets = new double[train.size()];
		/*List<ru.iitp.proling.ml.core.Instance> trainVectors = new ArrayList<ru.iitp.proling.ml.core.Instance>();
		int i = 0;
		for(Instance inst : train){
			trainVectors.add(new MalletInstance((SparseVector)inst.getData()));
			targets[i++] = (Double)labels.lookupObject(((Label)inst.getTarget()).getIndex());
		}
		WeightVector wv = new WeightVectorMallet(trainVectors, targets, new LinearKernel());
		WeightVector wv1 = new WeightVectorLinear(trainSet, targets, new LinearKernel());
		
		WeightVector wvP = new WeightVectorMallet(trainVectors, targets, new LinearKernel());
		
		DCDSolver.verbosity = 5;
		DCDSolver.solve(wv, 0.05, 0.05, 500, 0.1, 100000);
		DCDSolver.solve(wv1, 0.05, 0.05, 500, 0.1, 100000);
		//Perceptron pc = new Perceptron(0.001, 10);
		
		//AdaBoost ab = new AdaBoost(pc, 100);
		
		//ab.solve(wvP);
		
		System.out.println("Norm: " + wv.norm());
		System.out.println("Loss:" + SVMBinarySolver.loss(wv));

		System.out.println("Ref norm: " + wv.norm());
		System.out.println("Ref loss:" + SVMBinarySolver.loss(wv));
		
		System.out.println("Perceptron norm: " + wvP.norm());
		System.out.println("Perceptron loss:" + SVMBinarySolver.loss(wv));*/



			
		

	}

}
