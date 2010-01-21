import java.util.ArrayList;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.List;
import java.beans.XMLEncoder;
import java.io.*;

import ru.iitp.proling.svm.*;


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
		//PolyKernel(1, 1, dset.max_dim())
		
		Kernel k = new PolyKernel(1, 1, dset.max_dim());
		WeightVector vw = new WeightVectorLinear(dset, targets, new HashKernel(k, k.dim(dset.max_dim())));
		DCDSolver.solve(vw, 0.05, 0.05, 500, 0.1, 1000000);
		

		
		System.out.println("Norm: " + Double.toString(vw.norm()));
		System.out.println("Loss:" + vw.loss());
		System.out.println("Primal objective:" + vw.objectivePrimal(0.05));
		System.out.println("Dual objective:" + vw.objectiveDual());
		System.out.println("Zero-one loss:" + vw.zero_one_loss());
		System.out.println(dset.alphabet());
		
		int m = 300000;
		int n = 1000000;
		
		longtest((long)m *n);
		
	}

}
