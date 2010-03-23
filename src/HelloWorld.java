import gnu.trove.TIntArrayList;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.beans.XMLEncoder;
import java.io.*;

import ru.iitp.proling.svm.*;
import ru.iitp.proling.svm.kernel.Kernel;
import ru.iitp.proling.svm.kernel.LinearKernel;
import ru.iitp.proling.svm.kernel.PolyKernel;
import ru.iitp.proling.svm.kernel.HashKernel;



public class HelloWorld {
	
	public static void longtest(long k){
		System.out.println(k);
	}
	
	
	public static void main(String[] args){
		
		if(args.length > 0)
			System.out.println(args[0]);
		else{
			System.out.println("Usage: program [filename]");
			return;
		}
		
		
		
		BasicDataset dset = new BasicDataset(args[0]);
		BasicDataset dtest= new BasicDataset(args[1]);
	
		System.out.println("Dataset size:" + Integer.toString(dset.size()));
		System.out.println("Dataset dim:" + Integer.toString(dset.dim()));
		
		double[] targets = dset.labels();
		//int[] t = dset.targets();
		
		//for(int i = 0; i != dset.size(); i++)
		//	targets[i] = t[i] == 1? 1.0 : -1.0;
		
		double[] costs = new double[dset.size()];
		Arrays.fill(costs, 1.0);
		//PolyKernel(1, 1, dset.max_dim())
		
		//Kernel k = new PolyKernel(1, 1, dset.max_dim());
		Kernel k = new LinearKernel();
		WeightVector vw = new WeightVectorLinear(dset, targets, k);
		DCDSolver.verbosity = 0;
		//DCDSolver.solve(vw, 0.05, 0.05, 500, 0.1, 1000000);
		ArrayList<Integer> q = new ArrayList<Integer>();
		HashSet<Integer> qds = new HashSet<Integer>();
		
		for(int i : dset.qids()){
			q.add(i);
		}
		
		//RankBoost rb = new RankBoost(10, new LinearKernel());
		DCDSolver.verbosity = 1;
		
		//Scorer s = DCDSolver.solve(new WeightVectorRanking(vw, q), 0.05, 0.05, 500, 0.1, 1000000);
		
		WeightVectorRanking wvr = new WeightVectorRanking(vw, q);
		
		Scorer s_naive = DCDSolver.solve(wvr, 0.05, 0.05, 500, 0.1, 1000000);
		System.out.println("SVMRank.");
		SimpleRanker srn = new SimpleRanker(s_naive);
		ClassifierEval.evalRanker(dset, srn);
		ClassifierEval.evalRanker(dtest, srn);
		
		wvr.clear();

		
		
		/*//Scorer s = RankBoost.train(wv, new DCDSolver(0.05, 0.05, 500, 0.1, 10000000), 2, q);
		Scorer s = RankBoost.train(wvr, new DCDSolver(0.001, 0.001, 5000, 0.1, 1000000), 5);
		SimpleRanker sr = new SimpleRanker(s);
		System.out.println("RankBoost.");
		ClassifierEval.evalRanker(dset, sr);
		ClassifierEval.evalRanker(dtest, sr);
		// list of instancelists indexes
		
		//MulticlassCS mc = new MulticlassCS(dset, dset.targets(), 0.05, 0.1, 1000);
		//mc.solve();
		
		//AdaBoostDTrainer tr = new AdaBoostDTrainer(10, new LinearKernel(), 0.05);
		
		//tr.train(vw, new DCDSolver(0.05, 0.05, 500, 0.1, 1000000));
		
		//System.out.println("Adaboost zero-one loss:" + tr.zero_one_loss(dset, targets));
		
		
		//System.out.println("MC dual objective:" +  mc.dual_objective());
		//System.out.println("MC Zero-one loss:" + mc.zero_one_loss());
		

		
		System.out.println("Norm: " + Double.toString(vw.norm()));
		System.out.println("Loss:" + vw.loss());
		System.out.println("Primal objective:" + vw.objectivePrimal(0.05));
		System.out.println("Dual objective:" + vw.objectiveDual());
		System.out.println("Zero-one loss:" + vw.zero_one_loss());
		//System.out.println(dset.alphabet());
		
		
		*/
	}

}
