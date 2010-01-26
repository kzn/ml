import java.util.ArrayList;

import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.List;
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
		
		
		
		Dataset dset = new BasicDataset(args[0]);
	
		System.out.println("Dataset size:" + Integer.toString(dset.size()));
		System.out.println("Dataset dim:" + Integer.toString(dset.max_dim()));
		
		double[] targets = new double[dset.size()];
		int[] t = dset.targets();
		
		for(int i = 0; i != dset.size(); i++)
			targets[i] = t[i] == 1? 1.0 : -1.0;
		
		double[] costs = new double[dset.size()];
		Arrays.fill(costs, 1.0);
		//PolyKernel(1, 1, dset.max_dim())
		
		//Kernel k = new PolyKernel(1, 1, dset.max_dim());
		Kernel k = new LinearKernel();
		WeightVector vw = new WeightVectorLinear(dset, targets, costs, k);
		//DCDSolver.solve(vw, 0.05, 0.05, 500, 0.1, 1000000);
		//MulticlassCS mc = new MulticlassCS(dset, dset.targets(), 0.05, 0.1, 1000);
		//mc.solve();
		
		AdaBoostTrainer tr = new AdaBoostTrainer(10, new LinearKernel());
		
		tr.train(dset, targets, 10);
		
		System.out.println("Adaboost zero-one loss:" + tr.zero_one_loss(dset, targets));
		
		//System.out.println("MC dual objective:" +  mc.dual_objective());
		//System.out.println("MC Zero-one loss:" + mc.zero_one_loss());
		

		
		System.out.println("Norm: " + Double.toString(vw.norm()));
		System.out.println("Loss:" + vw.loss());
		System.out.println("Primal objective:" + vw.objectivePrimal(0.05));
		System.out.println("Dual objective:" + vw.objectiveDual());
		System.out.println("Zero-one loss:" + vw.zero_one_loss());
		//System.out.println(dset.alphabet());
		
		
		
	}

}
