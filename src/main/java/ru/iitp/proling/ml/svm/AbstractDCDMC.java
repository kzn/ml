package ru.iitp.proling.ml.svm;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import ru.iitp.proling.common.ArrayUtils;
import ru.iitp.proling.ml.core.DatasetUtils;
import ru.iitp.proling.ml.core.Instance;

/**
 * Multiclass DCD solver in SVMstruct style
 * @author Anton Kazennikov
 *
 */
public abstract class AbstractDCDMC {
//	List<Instance> dataset;
//	double w[];
//	double targets[];
	int nClasses;
	
//	protected AbstractDCDMC(double c_pos, double c_neg, int iter, double eps, int threshold) {
//		super(c_pos, c_neg, iter, eps, threshold);
//	}
//	
//	/**
//	 * Return current value of the weight vector
//	 */
//	public double[] w() {
//		return w;
//	}
//	
//	public abstract int h(int vec, int index);
//
//	/**
//	 * Construct solver
//	 * @param dataset sample dataset
//	 * @param targets sample target values
//	 * @param c_pos C for positive samples
//	 * @param c_neg C for negative examples
//	 * @param iter max number of iterations to perform
//	 * @param eps epsilon to stop within optimal solution
//	 * @param threshold active vars threshold between shuffling and sorting
//	 */
//	public AbstractDCDMC(List<Instance> dataset, double[] targets, int dim, int nClasses, double c_pos, double c_neg, 
//			int iter, double eps, int threshold) {
//		super(c_pos, c_neg, iter, eps, threshold);
//		this.dataset = dataset;
//		this.targets = targets;
//		w = new double[dim + 1];
//		this.nClasses = nClasses;
//	}
//	
//
//	@Override
//	public void init() {
//		for(int i = 0; i != w.length; i++) {
//			w[i] = 0;
//		}
//	}
//
//	@Override
//	public double snorm(int vec) {
//		vec = vec / size();
//		
//		Instance v = dataset.get(vec);
//		double snorm = 0;
//		for(int i = 0; i != v.size(); i++) {
//			snorm += v.valueAt(i) * v.valueAt(i);
//		}
//
//		return snorm;
//	}
//
//	@Override
//	public int size() {
//		return dataset.size();
//	}
//
//	@Override
//	public double dot(int vec) {
//		int c = vec % size();
//		vec = vec / size();
//		
//		Instance v = dataset.get(vec);
//		double val = 0;
//		
//		for(int i = 0; i != v.size(); i++) {
//			val += w[h(c, v.indexAt(i))] * v.valueAt(i);
//		}
//
//		return val;
//	}
//
//	@Override
//	public void add(int vec, double factor) {
//		Instance v = dataset.get(vec);
//		
//		for(int i = 0; i != v.size(); i++) {
//			w[v.indexAt(i)] += factor * v.valueAt(i);
//		}
//
//	}
//
//	@Override
//	public double target(int vec) {
//		return targets[vec];
//	}
//



	protected int verbosity;
	double c_pos;
	double c_neg;
	int iter;
	double eps;
	int threshold;

	public AbstractDCDMC(double c_pos, double c_neg, int iter, double eps, int threshold) {
		this.c_pos = c_pos;
		this.c_neg = c_neg;
		this.iter = iter;
		this.eps = eps;
		this.threshold = threshold;

	}

	public abstract void init();

	public abstract int size();
	public abstract int numClasses();

	public abstract double target(int c, int vec);

	public abstract double dot(int c, int vec);
	public abstract double snorm(int vec);
	public abstract void add(int c, int vec, double factor);

	public void setVerbosity(int verbosity) {
		this.verbosity = verbosity;
	}









	/**
	 * Solve the SVM problem for given loss norm. 
	 * @param norm loss norm. norm = 1 => hinge loss, 2 => squared hinge loss
	 */
	public void solve(int norm) {
		init();

		int size = size();
		int numClasses = numClasses();
		int[] index = new int[size * numClasses];
		double[] alphas = new double[size * numClasses];
		double[] snorms = new double[size];
		int samples = numClasses * size;

		int active = samples; // number of active variables in current iteration
		
		int iters = 0;

		double max_pg_pos = Double.POSITIVE_INFINITY; // maximum positive projected gradient(PG)
		double min_pg_neg = Double.NEGATIVE_INFINITY; // minimum negative projected gradient(PG)

		for(int i = 0; i != size; i++) {
			snorms[i] = snorm(i);
		}
		
		for(int i = 0; i < index.length; i++) {
			index[i] = i;
		}


		if(verbosity > 0){
			System.out.printf("C:%.4f/%.4f\n",c_pos, c_neg);
			System.out.println("Algorithm: DCD-MC");
			System.out.printf("Problem size: %d samples (%d classes)%n", size, numClasses);
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

					
					int cls = i % numClasses;
					int x = i / numClasses;


					double alpha = alphas[i]; 
					double target = target(cls, x);
					double cost = target == 1.0? c_pos : c_neg;
					double d_ii = norm == 2? 0.5/cost : 0;
					double g = 0;

					g = dot(cls, x);
					
					g *= target;
					g -= 1;
					g += alpha*d_ii;

					boolean shrink = false;

					double c = norm == 1? cost : Double.POSITIVE_INFINITY;

					double pg = g; // projected gradient
					if(alpha == 0) {
						pg = Math.min(g, 0.0);
						shrink = g > max_pg_pos;
					} else if(alpha == c) {
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

					if(pg != 0.0) {
						double alpha_old = alpha;
						double q = snorms[x] + d_ii;
						double alpha_new = Math.min(Math.max(alpha - g/q, 0.0), c);
						alphas[i] += alpha_new - alpha_old;
						double f = target*(alpha_new - alpha_old);
						add(cls, x, f);
					}
				
			}
			
			if(verbosity > 1)
				System.out.printf("Iter %d: active: %d\t eps=%.4f\t elapsed: %d msecs\n", t, active, max_pg - min_pg, 
						System.currentTimeMillis() - iter_time);
			double diff = max_pg - min_pg;

			if(diff <= eps && active == samples) {
				if(verbosity > 0) {
					System.out.printf("Reached min eps at: %d, ", t);
					System.out.printf("eps: %.4f%n",diff);
				}
				break;
			} else if(diff <= eps) {
				if(verbosity > 2){
					System.out.print('*');
					System.out.flush();
				}
				active = samples;
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
			System.out.printf("Optimization done in: %.2f secs\n", elapsed / 1.0E3); 
			System.out.println("Done.");
		}
	}
	
}
