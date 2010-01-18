package ru.iitp.proling.svm;

import java.util.Random;

public class PegasosSolver {
	public static int ITER_PER_DOT = 1000;
	
	public static void solve(WeightVector w, int iter, int k, double lambda, int start_iter){
		  int totdocs = w.size();
		  
		  Random gen = new Random();
		  
		  
		  if(k == 0)
			  k = totdocs;
 
		  int[] ex = new int[k];
		  int ptr = 0; // index of A+ array
		  
		  
		  
		  
		  for(int t = 1; t != iter; t++){
		    ptr = 0;

		    if(t % ITER_PER_DOT == 0){
		    	System.out.print(".");
		    	System.out.flush();
		    }
		    
		    double eta = 1.0/(lambda * (t + start_iter));

		    for(int i = 0; i != k; i++){
		      int idx = Math.abs(gen.nextInt()) % totdocs; 
		      
		      double eval = w.target(idx) * w.dot(idx);
		      if(eval - 1.0 < 10e-12)
		    	  ex[ptr++] = idx;
		    }

		    w.scale(1 - 1.0/(t + start_iter));

		    for(int i = 0; i != ptr; i++){
		      w.add(ex[i], eta/k*w.target(ex[i]));
		      w.add_alpha(ex[i], eta / k);
		    }


		    double snorm = w.snorm();
		    if(snorm > 1/lambda)
		      w.scale(Math.sqrt(1/(lambda * snorm)));
		  }
		  System.out.println("Done.");
		} 

}
