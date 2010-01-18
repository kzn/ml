import java.util.ArrayList;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.List;
import java.io.*;
import ru.iitp.proling.svm.*;


public class HelloWorld {
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
		
		WeightVector vw = new WeightVectorLinear(dset, new LinearKernel());
		
		DCDSolver.solve(vw, 0.05, 0.05, 500, 0.1, 100000);
		
		System.out.println("Norm: " + Double.toString(vw.norm()));
		
		
		
		
		
		
		
	}

}
