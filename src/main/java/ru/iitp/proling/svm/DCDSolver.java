package ru.iitp.proling.svm;
import java.util.*;

import name.kazennikov.common.ArrayUtils;
import name.kazennikov.ml.core.Instance;
import ru.iitp.proling.ml.core.Boostable;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.ml.scorer.TrivialScorer;
import ru.iitp.proling.svm.kernel.Kernel;
import ru.iitp.proling.svm.kernel.LinearKernel;

public class DCDSolver extends BinarySolver implements Boostable{
	protected double c_pos;
	protected double c_neg;
	protected int iter;
	protected double eps;
	protected int threshold;
	public static int verbosity = 0;

	
	public DCDSolver(double c_pos, double c_neg, int iter, double eps, int threshold){
		this.c_pos = c_pos;
		this.c_neg = c_neg;
		this.iter = iter;
		this.eps = eps;
		this.threshold = threshold;
	}
	
	@Override
	public Scorer solve(WeightVector wv) {
		return solve(wv, c_pos, c_neg, iter, eps, threshold, 1); 
	}
	
	
	/**
	 * Dual Coordinate Descent L1-SVM and L2-SVM solver function.
	 * For details cf.Algorithm 3 from "A Dual Coordinate Descent Method for Large-scale Linear SVM" 
	 * @param wv SVM problem formulation
	 * @param c_pos C for positive samples
	 * @param c_neg C for negative samples
	 * @param iter maximum number of iterations to perform
	 * @param eps epsilon for PG optimal check
	 * @param threshold threshold for switching between sequential and random samples order
	 * @param norm degree of loss norm to use, 1 - L1-SVM, 2 - L2-SVM
	 * @return scorer that computes f(x) = w*x
	 */
	public static TrivialScorer solve(WeightVector wv, double c_pos, double c_neg, int iter, double eps, int threshold, int norm){
		
		int totdocs = wv.size(); // problem size
	    int[] index = new int[totdocs];
	    double[] svm_cost = new double[totdocs]; // cost of misclassification of each sample 
	    int active = totdocs; // number of active variables in current iteration
	    int iters = 0;
	    double max_pg_pos = Double.POSITIVE_INFINITY; // maximum positive projected gradient(PG)
	    double min_pg_neg = Double.NEGATIVE_INFINITY; // minimum negative projected gradient(PG)

	    
	    for(int i = 0; i != totdocs; i++){
	    	index[i] = i;
	    	double cost = wv.target(i) == 1? c_pos : c_neg;
	    	svm_cost[i] = cost * wv.cost(i);
	    }

	    
	    if(verbosity > 0){
	    	System.out.printf("C:%f/%f\n",c_pos, c_neg);
	    	System.out.println("Algorithm: DCD Full");
	    	System.out.printf("Problem size: %d, dim: %d%n", wv.size(), wv.dim());
	    }
	    
	    long elapsed = System.nanoTime();
	    
	    for(int t = 1; t != iter; t++){
	    	double max_pg = Double.NEGATIVE_INFINITY;
	    	double min_pg = Double.POSITIVE_INFINITY;
	    	
	    	long iter_time = System.nanoTime();
	    	
	    	if(threshold > 0 || active <= threshold)
	    		ArrayUtils.shuffle(index, active);
	    	else
	    		Arrays.sort(index, 0, active);
	    	
	    	for(int j = 0; j != active; j++){
	    		iters++;
	    		int i = index[j];
	    		double alpha = wv.alpha(i);
	    		double target = wv.target(i);
	    		double d_ii = norm == 2? 0.5/svm_cost[i] : 0;
	    		double g = wv.dot(i)*target - 1 + alpha * d_ii; // gradient
	    		
	    		
	    		
	    		 

	    		boolean shrink = false;
	    		
	    		double c = norm == 1? svm_cost[i] : Double.POSITIVE_INFINITY;
	    		
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
	    			double q = wv.snorm(i) + d_ii;
	    			double alpha_new = Math.min(Math.max(alpha - g/q, 0.0), c);
	    			wv.add_alpha(i, alpha_new - alpha_old);
	    			wv.add(i, target*(alpha_new - alpha_old));
			    }
	    	}
	    	if(verbosity > 1)
	    		System.out.printf("Iter %d: active: %d\t eps=%f\t elapsed: %d msecs\n", t, active, max_pg - min_pg, (System.nanoTime() - iter_time)/1000000);
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
	    	System.out.printf("Optimization done in: %f secs\n", elapsed*1.0E-9);
	    	System.out.println("Done.");
	    }
		return new TrivialScorer(wv.vec(), wv.kernel());
	}
	
	public static TrivialScorer solve(List<Instance> ds, double[] targets, double c_pos, double c_neg, int iter, double eps, int threshold, int norm){
		
		int totdocs = ds.size(); // problem size
		int dim = 0;
		Kernel kernel = new LinearKernel();
		
		for(Instance inst : ds)
			dim = Math.max(dim, inst.dim());
		
		dim = kernel.dim(dim);
		
	    int[] index = new int[totdocs];
	    double[] w = new double[dim + 1];
	    double[] alphas = new double[totdocs];
	    double[] snorms = new double[totdocs];
	    double[] costs = new double[totdocs];
	    
	    int active = totdocs; // number of active variables in current iteration
	    int iters = 0;
	    double max_pg_pos = Double.POSITIVE_INFINITY; // maximum positive projected gradient(PG)
	    double min_pg_neg = Double.NEGATIVE_INFINITY; // minimum negative projected gradient(PG)
	    
	    for(int i = 0; i != totdocs; i++){
	    	index[i] = i;
	    	costs[i] = targets[i] == 1.0? c_pos : c_neg;
	    	snorms[i] = kernel.snorm(ds.get(i));
	    }

	    
	    if(verbosity > 0){
	    	System.out.printf("C:%f/%f\n",c_pos, c_neg);
	    	System.out.println("Algorithm: DCD Full");
	    	System.out.printf("Problem size: %d, dim: %d%n", ds.size(), dim);
	    }
	    
	    long elapsed = System.nanoTime();
	    
	    for(int t = 1; t != iter; t++){
	    	double max_pg = Double.NEGATIVE_INFINITY;
	    	double min_pg = Double.POSITIVE_INFINITY;
	    	
	    	long iter_time = System.nanoTime();
	    	
	    	if(threshold > 0 || active <= threshold)
	    		ArrayUtils.shuffle(index, active);
	    	else
	    		Arrays.sort(index, 0, active);
	    	
	    	for(int j = 0; j != active; j++){
	    		iters++;
	    		int i = index[j];
	    		double alpha = alphas[i]; 
	    		double target = targets[i];
	    		double d_ii = norm == 2? 0.5/costs[i] : 0;
	    		double g = 0;
	    		Instance vec = ds.get(i);
	    		
	    		g = kernel.dot(w, vec);
	    		
	    		g *= target;
	    		g -= 1;
	    		g += alpha*d_ii;
	    		
	    		
	    		
	    		 

	    		boolean shrink = false;
	    		
	    		double c = norm == 1? costs[i] : Double.POSITIVE_INFINITY;
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
	    			kernel.add(w, vec, f);

			    }
	    	}
	    	if(verbosity > 1)
	    		System.out.printf("Iter %d: active: %d\t eps=%f\t elapsed: %d msecs\n", t, active, max_pg - min_pg, (System.nanoTime() - iter_time)/1000000);
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
	    	System.out.printf("Optimization done in: %f secs\n", elapsed*1.0E-9);
	    	double n = 0; 
	    	for(double d : w)
	    		n += d * d;
	    	System.out.printf("Norm:%f%n", Math.sqrt(n));
	    	System.out.println("Done.");
	    }
		return new TrivialScorer(w, new LinearKernel());
	}
	
	
	public static TrivialScorer solve(List<Instance> ds, double[] targets, double c_pos, double c_neg, double[] weights, int iter, double eps, int threshold, int norm){
		
		int totdocs = ds.size(); // problem size
		int dim = 0; 
		
		for(Instance inst : ds)
			dim = Math.max(dim, inst.dim());
		
	    int[] index = new int[totdocs];
	    double[] w = new double[dim + 1];
	    double[] alphas = new double[totdocs];
	    double[] snorms = new double[totdocs];
	    double[] costs = new double[totdocs];
	    
	    Kernel kernel = new LinearKernel();
	     
	    int active = totdocs; // number of active variables in current iteration
	    int iters = 0;
	    double max_pg_pos = Double.POSITIVE_INFINITY; // maximum positive projected gradient(PG)
	    double min_pg_neg = Double.NEGATIVE_INFINITY; // minimum negative projected gradient(PG)
	    
	    for(int i = 0; i != totdocs; i++){
	    	index[i] = i;
	    	costs[i] = targets[i] == 1.0? c_pos : c_neg;
	    	costs[i] *= weights[i];
	    	snorms[i] = kernel.snorm(ds.get(i));
	    }

	    
	    if(verbosity > 0){
	    	System.out.printf("C:%f/%f\n",c_pos, c_neg);
	    	System.out.println("Algorithm: DCD Full");
	    	System.out.printf("Problem size: %d, dim: %d%n", ds.size(), dim);
	    }
	    
	    long elapsed = System.nanoTime();
	    
	    for(int t = 1; t != iter; t++){
	    	double max_pg = Double.NEGATIVE_INFINITY;
	    	double min_pg = Double.POSITIVE_INFINITY;
	    	
	    	long iter_time = System.nanoTime();
	    	
	    	if(threshold > 0 || active <= threshold)
	    		ArrayUtils.shuffle(index, active);
	    	else
	    		Arrays.sort(index, 0, active);
	    	
	    	for(int j = 0; j != active; j++){
	    		iters++;
	    		int i = index[j];
	    		double alpha = alphas[i]; 
	    		double target = targets[i];
	    		double g = 0;
	    		Instance vec = ds.get(i);
	    		
	    		g = kernel.dot(w, vec);
	    		
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
	    			kernel.add(w, vec, f);

			    }
	    	}
	    	if(verbosity > 1)
	    		System.out.printf("Iter %d: active: %d\t eps=%f\t elapsed: %d msecs\n", t, active, max_pg - min_pg, (System.nanoTime() - iter_time)/1000000);
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
	    	System.out.printf("Optimization done in: %f secs\n", elapsed*1.0E-9);
	    	double n = 0; 
	    	for(double d : w)
	    		n += d * d;
	    	System.out.printf("Norm:%f%n", Math.sqrt(n));
	    	System.out.println("Done.");
	    }
		return new TrivialScorer(w, new LinearKernel());
	}




	
	
	
}
	

