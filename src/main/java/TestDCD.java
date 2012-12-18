
import gnu.trove.list.array.TDoubleArrayList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.net.ssl.SSLContext;

import name.kazennikov.ml.core.Instance;

import cc.mallet.types.SparseVector;

//import cc.mallet.types.Instance;

import ru.iitp.proling.ml.core.MalletInstance;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.BinaryClassifier;
import ru.iitp.proling.svm.ClassifierEval;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.RWSample;
import ru.iitp.proling.svm.RankBoost;
import ru.iitp.proling.svm.SVMBinarySolver;
import ru.iitp.proling.svm.WeightVectorCost;

import ru.iitp.proling.svm.SimpleRanker;

import ru.iitp.proling.svm.WeightVectorLinear;
import ru.iitp.proling.svm.WeightVectorRanking;
//import ru.iitp.proling.svm.kernel.FastLinearKernel;
import ru.iitp.proling.svm.kernel.Kernel;
import ru.iitp.proling.svm.kernel.LinearKernel;
import ru.iitp.proling.svm.kernel.PolyHashKernel;
import ru.iitp.proling.svm.kernel.PolyKernel;
import ru.iitp.proling.svm.kernel.wAccessorHash;


public class TestDCD {
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		if(args.length > 0)
			System.out.println(args[0]);
		else{
			System.out.println("Usage: program [filename]");
			return;
		}
		
		
		 
		long elapsed = System.nanoTime();
		BasicDataset dset = BasicDataset.readBinary(args[0]);
		//dset = new BasicDataset(args[0]);
		
		elapsed = System.nanoTime() - elapsed;
		System.out.printf("Elapsed on reading dataset: %f secs%n", elapsed/1.0E9);
		
		//BasicDataset dtest = new BasicDataset(args[1]);
		
		
		double[] targets = new double[dset.size()];
		double[] costs = new double[dset.size()];
		Arrays.fill(costs, 1.0);
		for(int i = 0; i != dset.size(); i++){
			targets[i] = dset.target(i);
			/*if(targets[i] == 1)
				costs[i] = 0.1;*/
				
			
		}
		
		int D = 150;
		Kernel k = new PolyHashKernel(32, 30, dset.dim(), D);
		//k = new FastLinearKernel();
		k = new LinearKernel();
		//k = new PolyKernel(1, 4, dset.dim());
		//k.setwa(new wAccessorHash(500));
		
		WeightVectorLinear wvl = new WeightVectorLinear(dset.instances(), targets, k);
		WeightVector wv = new WeightVectorCost(wvl, costs);
		
		
		DCDSolver.verbosity = 1;
		/*Scorer s = DCDSolver.solve(wv, 0.05, 0.05, 500, 0.01, 10000, 1);
		System.out.printf("Norm: %f%n", wv.norm());
		System.out.printf("Loss: %f%n", SVMBinarySolver.loss(wv));
		System.out.printf("1/0 loss:%f\n", SVMBinarySolver.zero_one_loss(wv));
		System.out.printf("Dual: %f%n", SVMBinarySolver.objectiveDual(wv));*/
		List<Instance> inst = new ArrayList<Instance>();
		TDoubleArrayList tgts = new TDoubleArrayList();
		for(int i = 0; i != dset.size(); i++){
			//inst.add(dset.get(i));
			RWSample<Double> s = dset.get(i);
			//inst.add(new MalletInstance(new SparseVector(s.indexes(), s.values())));
			inst.add(s);
			tgts.add(dset.target(i));
		}
		Scorer s1;
		long t = System.nanoTime();
		for(int i = 0; i != 50; i++)
			s1 = DCDSolver.solve(inst, tgts.toArray(), 0.05, 0.05, 500, 0.1, 100000, 1);
		
		System.out.printf("Overall time: %f secs%n", (System.nanoTime() - t)*1.0E-9);
		/*if(args.length == 2){
			BasicDataset dtest = BasicDataset.readBinary(args[1]);
			ClassifierEval.evalBinaryClassifier(dtest, new BinaryClassifier<Double>(s, 1.0, -1.0));
			
		}*/
		//BinaryClassifier<Double> cl = new BinaryClassifier<Double>(s, 1.0, -1.0);
		
		//cl.write(args[0] + ".model");
		
		
		
		
		
		//AdaBoost adaboost = new AdaBoost(new DCDSolver(1, 1, 100, 0.1, 100000), 50);
		//Scorer s = adaboost.solve(wv);
		
		
		
	/*	int errs = 0;
		for(int i = 0; i != dtest.size(); i++){
			double label = dtest.alphabet().get(dtest.targets()[i]);
			double cl = s.score(dtest.get(i)) > 0? 1 : -1;
			if(cl * label < 0)
				errs++;
		}
		
		
	
		
		System.out.println("Dataset size:" + Integer.toString(dset.size()));
		System.out.println("Dataset dim:" + Integer.toString(dset.dim()));
		
		System.out.printf("Errors on test: %d/%d%n", errs, dtest.size());
		
		*/
		

	}

}
