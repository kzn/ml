package name.kazennikov.ml.svm;

import java.util.Arrays;

import name.kazennikov.common.ArrayUtils;

/**
 * Dual Coordinate Descent solver for Linear SVM<br>
 * 
 * The solver assumes following problem setting:<br><ul>
 * <li> there is a dataset of fixed size
 * <li> it's dimensions are known and fixed
 * </ul><br>
 * 
 * Then the solver can operate with indexes of training vectors instead of vectors themselves
 * 
 * @author Anton Kazennikov
 *
 */
public abstract class AbstractDCD {
	
	protected double c_pos;
	protected double c_neg;
	protected int iter;
	protected double eps;
	protected int threshold;
	protected int verbosity;

	/**
	 * Base constructor for DCD Solver for Linear SVMs
	 * @param c_pos C for positive samples
	 * @param c_neg C for negative samples
	 * @param iter maximum number iterations to perform
	 * @param eps epsilon for optimal solution
	 * @param threshold if  > 0, then if number of active vars is less than threshold, then shuffle them, 
	 * instead of just linear sorting (used only in very large datasets, that don't fit in RAM)
	 */
	public AbstractDCD(double c_pos, double c_neg, int iter, double eps, int threshold){
		this.c_pos = c_pos;
		this.c_neg = c_neg;
		this.iter = iter;
		this.eps = eps;
		this.threshold = threshold;
	}
	
	public void setVerbosity(int verbosity) {
		this.verbosity = verbosity;
	}
	
	/**
	 * Perform problem initialization
	 */
	public abstract void init();
	/**
	 * Return squared norm of sample vector
	 * @param vec index of the vector
	 * @return
	 */
	public abstract double snorm(int vec);

	/**
	 * Return problem size - number of samples in the dataset
	 */
	public abstract int size();
	
	/**
	 * Compute dot product &lt;w,x&gt; between given vector and weight vector
	 * @param vec index of sample vector
	 * @return
	 */
	public abstract double dot(int vec);
	/**
	 * Add factored given vector to current weight vector w = w + factor * vec
	 * @param vec index of sample vector
	 * @param factor vector factor
	 */
	public abstract void add(int vec, double factor);
	
	/**
	 * Get target value for given vector
	 * @param vec index of sample vector
	 */
	public abstract double target(int vec);
	
	/**
	 * Solve the SVM problem for given loss norm. 
	 * @param norm loss norm. norm = 1 => hinge loss, 2 => squared hinge loss
	 */
	public void solve(int norm) {
		init();

		int size = size(); // problem size
	    int[] index = new int[size];
	    double[] alphas = new double[size];
	    double[] snorms = new double[size];
	    //double[] costs = new double[size];
	    
	    int active = size; // number of active variables in current iteration
	    int iters = 0;
	    double max_pg_pos = Double.POSITIVE_INFINITY; // maximum positive projected gradient(PG)
	    double min_pg_neg = Double.NEGATIVE_INFINITY; // minimum negative projected gradient(PG)
	    
	    for(int i = 0; i != size; i++){
	    	index[i] = i;
	    	//costs[i] = target(i) == 1.0? c_pos : c_neg;
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
	    		double cost = target(i) == 1.0? c_pos : c_neg;
	    		double d_ii = norm == 2? 0.5/cost : 0;
	    		double g = 0;
	    		//Instance vec = ds.get(i);
	    		
	    		g = dot(i);
	    		
	    		g *= target;
	    		g -= 1;
	    		g += alpha*d_ii;
	    		
	    		
	    		
	    		 

	    		boolean shrink = false;
	    		
	    		double c = norm == 1? cost : Double.POSITIVE_INFINITY;
	    		//double c = target == 1.0? c_pos : c_neg;
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
	    			double q = snorms[i] + d_ii;
	    			double alpha_new = Math.min(Math.max(alpha - g/q, 0.0), c);
	    			alphas[i] += alpha_new - alpha_old;
	    			double f = target*(alpha_new - alpha_old);
	    			add(i, f);

			    }
	    	}
	    	if(verbosity > 1)
	    		System.out.printf("Iter %d: active: %d\t eps=%f\t elapsed: %d msecs\n", t, active, max_pg - min_pg, 
	    				System.currentTimeMillis() - iter_time);
	    	double diff = max_pg - min_pg;

			if(diff <= eps && active == size){
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
				active = size;
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
	    
	    elapsed = System.currentTimeMillis() - elapsed;
	    
	    if(verbosity > 0){
	    	System.out.printf("Optimization done in: %f secs\n", elapsed / 1.0E3); 
	    	System.out.println("Done.");
	    }
	}
	
	




}
