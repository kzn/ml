package name.kazennikov.ml.svm;

import java.util.Arrays;

import name.kazennikov.common.ArrayUtils;
/**
 * Weighted DCD Solver for Linear SVMs. Each sample could have a weight
 * to measure importance of its misclassification
 * 
 * @author Anton Kazennikov
 *
 */
public abstract class AbstractDCDWeighted extends AbstractDCD {
	
	public AbstractDCDWeighted(double c_pos, double c_neg, int iter, double eps, int threshold) {
		super(c_pos, c_neg, iter, eps, threshold);
	}

	/**
	 * Get weight of sample vector
	 * @param vec vector index
	 * @return
	 */
	abstract double weight(int vec);
	
	@Override
	public void solve(int norm){
		
		int totdocs = size();
		
		
	    int[] index = new int[totdocs];
	    double[] alphas = new double[totdocs];
	    double[] snorms = new double[totdocs];
	    double[] costs = new double[totdocs];
	    
	     
	    int active = totdocs; // number of active variables in current iteration
	    int iters = 0;
	    double max_pg_pos = Double.POSITIVE_INFINITY; // maximum positive projected gradient(PG)
	    double min_pg_neg = Double.NEGATIVE_INFINITY; // minimum negative projected gradient(PG)
	    
	    for(int i = 0; i != totdocs; i++){
	    	index[i] = i;
	    	costs[i] = target(i) == 1.0? c_pos : c_neg;
	    	costs[i] *= weight(i);
	    	snorms[i] = snorm(i);
	    }

	    
	    if(verbosity > 0){
	    	System.out.printf("C:%f/%f\n",c_pos, c_neg);
	    	System.out.println("Algorithm: DCD Full");
	    	System.out.printf("Problem size: %dn", size());
	    }
	    
	    long elapsed = System.currentTimeMillis();
	    
	    for(int t = 1; t != iter; t++){
	    	double max_pg = Double.NEGATIVE_INFINITY;
	    	double min_pg = Double.POSITIVE_INFINITY;
	    	
	    	long iter_time = System.currentTimeMillis();
	    	
	    	if(threshold > 0 || active <= threshold)
	    		ArrayUtils.shuffle(index, active);
	    	else
	    		Arrays.sort(index, 0, active);
	    	
	    	for(int j = 0; j != active; j++){
	    		iters++;
	    		int i = index[j];
	    		double alpha = alphas[i]; 
	    		double target = target(i);
	    		double g = 0;
	    		
	    		g = dot(i);
	    		
	    		g *= target;
	    		g -= 1;
	    		 

	    		boolean shrink = false;
	    		
	    		//double c = norm == 1? svm_cost[i] : Double.POSITIVE_INFINITY;
	    		double c = costs[i];
	    		//c *= costs[i];
	    		//double c = costs[i];
	    		
	    		double pg = g; // projected gradient
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
	    			ArrayUtils.swap(index, j, active);
	    			j--;
	    			continue;
		    	}
	    		
	    		if(pg != 0.0){
	    			double alpha_old = alpha;
	    			double q = snorms[i];
	    			double alpha_new = Math.min(Math.max(alpha - g/q, 0.0), c);
	    			alphas[i] += alpha_new - alpha_old;
	    			double f = target*(alpha_new - alpha_old);
	    			add(i, f);

			    }
	    	}
	    	if(verbosity > 1)
	    		System.out.printf("Iter %d: active: %d\t eps=%f\t elapsed: %d msecs\n", t, active, max_pg - min_pg, System.currentTimeMillis() - iter_time);
	    	double diff = max_pg - min_pg;

			if(diff <= eps && active == totdocs){
				if(verbosity > 0){
					System.out.println("Reached min eps at:" + Integer.toString(t));
					System.out.println("Eps:" + Double.toString(diff));
				}
				break;
			}else if(diff <= eps){
				if(verbosity > 2){
					System.out.print('*');
					System.out.flush();
				}
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
	    
	    if(verbosity > 0){
	    	System.out.printf("Optimization done in: %f secs\n", elapsed/1.0E3);
	    	System.out.println("Done.");
	    }
	}



}
