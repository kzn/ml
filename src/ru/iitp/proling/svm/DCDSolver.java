package ru.iitp.proling.svm;
import java.util.*;

public class DCDSolver extends BinarySolver{
	protected double c_pos;
	protected double c_neg;
	protected int iter;
	protected double eps;
	protected int threshold;
	
	public DCDSolver(double c_pos, double c_neg, int iter, double eps, int threshold){
		this.c_pos = c_pos;
		this.c_neg = c_neg;
		this.iter = iter;
		this.eps = eps;
		this.threshold = threshold;
	}
	
	@Override
	public void solve(WeightVector wv) {
		solve(wv, c_pos, c_neg, iter, eps, threshold);
	}
	
	protected static void swap(int[] array, int i, int j){
		int t = array[i];
		array[i] = array[j];
		array[j] = t;
	}
	
	protected static void shuffle(int[] array, int active){
		for(int i = 0; i != active; i++){
			int j = i + (int)(Math.random() * (active - i));
			swap(array, i, j);
		}
	}
	
	public static double[] solve(WeightVector wv, double c_pos, double c_neg, int iter, double eps, int threshold){
			
		
		int totdocs = wv.size();
	    double c = c_pos;
	    int[] index = new int[totdocs];
	    int active = totdocs;
	    int iters = 0;
	    double max_pg_pos = Double.POSITIVE_INFINITY;
	    double min_pg_neg = Double.NEGATIVE_INFINITY;
	    
	    for(int i = 0; i != totdocs; i++)
	    	index[i] = i;
	    
	    System.out.printf("C:%f/%f\n",c_pos, c_neg);
	    System.out.println("Algorithm: DCD Full");
	    
	    long elapsed = System.nanoTime();
	    
	    for(int t = 1; t != iter; t++){
	    	double max_pg = Double.NEGATIVE_INFINITY;
	    	double min_pg = Double.POSITIVE_INFINITY;
	    	
	    	long iter_time = System.nanoTime();
	    	
	    	if(threshold > 0 || active < threshold)
	    		shuffle(index, active);
	    	else
	    		Arrays.sort(index, 0, active);
	    	
	    	for(int j = 0; j != active; j++){
	    		iters++;
	    		int i = index[j];
	    		double alpha = wv.alpha(i);
	    		double target = wv.target(i);
	    		double g = wv.dot(i)*target - 1;
	    		double pg = g;
	    		boolean shrink = false;
	    		
	    		c = target == 1.0? c_pos : c_neg;
	    		
	    		if(alpha == 0){
	    			pg = Math.min(g, 0.0);
	    			shrink = g > max_pg_pos;
	    		}else if(alpha == c){
	    			pg = Math.max(g, 0);
	    			shrink = g < min_pg_neg;
	    		}
	    		
	    		min_pg = Math.min(min_pg, pg);
	    		max_pg = Math.max(max_pg, pg);
	    		
	    		if(shrink){
	    			active--;
	    			swap(index, j, active);
	    			j--;
	    			continue;
		    	}
	    		
	    		if(pg != 0.0){
	    			double alpha_old = alpha;
	    			double alpha_new = Math.min(Math.max(alpha - g/wv.snorm(i), 0.0), c);
	    			wv.add_alpha(i, alpha_new - alpha_old);
	    			wv.add(i, target*(alpha_new - alpha_old));
			    }
	    	}
	    	System.out.printf("Iter %d: active: %d\t eps=%f\t elapsed: %d msecs\n", t, active, max_pg - min_pg, (System.nanoTime() - iter_time)/1000000);
	    	double diff = max_pg - min_pg;

			if(diff <= eps && active == totdocs){
				System.out.println("Reached min eps at:" + Integer.toString(t));
				System.out.println("Eps:" + Double.toString(diff));
				break;
			}else if(diff <= 0.8*eps){
				System.out.print('*');
				System.out.flush();
				active = totdocs;
			    max_pg_pos = Double.POSITIVE_INFINITY;
			    min_pg_neg = Double.NEGATIVE_INFINITY;
			    continue; // perform full gradient check on next iteration
			}

			max_pg_pos = max_pg;
			if(max_pg <= 0) // max_pg_pos must be strictly positive
				max_pg_pos = Double.POSITIVE_INFINITY;

			min_pg_neg = min_pg;
			if(min_pg >= 0) // m must be strictly negative
				min_pg_neg = Double.NEGATIVE_INFINITY;
	    }
	    
	    elapsed = System.nanoTime() - elapsed;

	    System.out.printf("Optimization done in: %f secs\n", ((double)elapsed)/1000000000);
		System.out.println("Done.");
		return wv.vec();
	}

	
	
	
}
	

