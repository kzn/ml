import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import ru.iitp.proling.ml.mallet.SVMBinaryClassifierTrainer;
import ru.iitp.proling.svm.kernel.LinearKernel;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.NaiveBayes;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.Target2Label;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;


public class TestMalletFW {
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
			List<Integer> t = new ArrayList<Integer>();



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
				
				if(target != -1 && target != 1)
					continue;
				

				List<Integer> idxs = new ArrayList<Integer>();
				List<Double> vals = new ArrayList<Double>();
				int qid = 0;
				double cost = 1.0;


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
						Integer z = idx;
						idxs.add(dataAlphabet.lookupIndex(z));
						vals.add(ft.nval);

						
					}else{
						char c = field.charAt(0);

						switch(c){
						case 'q':
							qid = (int) ft.nval;
							break;
						case 'c':
							cost = ft.nval;
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

	


public static void main(String[] args) {
	if(args.length < 1)
		throw new IllegalArgumentException("Number of aguments must be greater than 0");
	
	Alphabet data = new Alphabet();
	data.lookupIndex(new Integer(0));
	LabelAlphabet labels = new LabelAlphabet();
	InstanceList train = load(args[0], data, labels);
	InstanceList test = load(args[1], data, labels);
	System.out.printf("Alphabet size after test set: %d%n", data.size());
	
	SVMBinaryClassifierTrainer svmtrainer = new SVMBinaryClassifierTrainer(1, 1, 0.1, 1000, 100000, new LinearKernel());
	Classifier c = svmtrainer.train(train);
	//NaiveBayesTrainer nbt = new NaiveBayesTrainer();
	//NaiveBayes c = nbt.train(train);
	
	
	System.out.printf("Naive bayes accuracy:%f%n", c.getAccuracy(test));
		
	

	}

}


