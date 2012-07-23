package ru.iitp.proling.ml.svm;

import java.util.Random;

public abstract class AbstractPegasos {
	public static int ITER_PER_DOT = 1000;
	protected int iter; 
	protected int k;
	protected double c;
	protected int start_iter;
		
	
	public AbstractPegasos(int iter, int k, double c, int start_iter){
		this.iter = iter;
		this.k = k;
		this.c = c;
		this.start_iter = start_iter;
		
	}
	
	public abstract void init();
	public abstract int size();
	public abstract double target(int vec);
	public abstract double dot(int vec);
	public abstract void add(int vec, double factor);
	public abstract void scale(double factor);
	public abstract double snorm();
	
	
	
	// this code assumes that c is fixed for all samples
	// Doesn't use w.cost(i)
	public void solve() {
		init();
		
		  int totdocs = size();
		  double lambda = 1.0/c/size();
		  
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
		      
		      double eval = target(idx) * dot(idx);
		      if(eval - 1.0 < 10e-12)
		    	  ex[ptr++] = idx;
		    }

		    scale(1 - 1.0/(t + start_iter + 1));

		    for(int i = 0; i != ptr; i++){
		      add(ex[i], eta/k*target(ex[i]));
		    }


		    double snorm = snorm();
		    if(snorm > 1/lambda)
		      scale(Math.sqrt(1/(lambda * snorm)));
		  }
		  System.out.println("Done.");
		}


}
