package name.kazennikov.ml.svm;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import com.google.common.primitives.Doubles;

import name.kazennikov.common.ArrayUtils;
import name.kazennikov.ml.core.DatasetUtils;
import name.kazennikov.ml.core.Instance;

/**
 * Multiclass DCD solver in SVMstruct style
 * @author Anton Kazennikov
 *
 */
public abstract class AbstractDCDMC {



	protected int verbosity;
	double c;
	int iter;
	double eps;
	int threshold;

	public AbstractDCDMC(double c, int iter, double eps, int threshold) {
		this.c = c;
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

		double[] max_pg_pos = new double[numClasses];
		Arrays.fill(max_pg_pos, Double.POSITIVE_INFINITY); // maximum positive projected gradient(PG)
		double[] min_pg_neg = new double[numClasses];
		Arrays.fill(min_pg_neg, Double.NEGATIVE_INFINITY); // minimum negative projected gradient(PG)

		for(int i = 0; i != size; i++) {
			snorms[i] = snorm(i);
		}
		
		for(int i = 0; i < index.length; i++) {
			index[i] = i;
		}


		if(verbosity > 0){
			System.out.printf("C=%.4f%n",c);
			System.out.println("Algorithm: DCD-MC");
			System.out.printf("Problem size: %d samples (%d classes)%n", size, numClasses);
		}

		long elapsed = System.currentTimeMillis();

		double[] max_pg = new double[numClasses];			
		double[] min_pg = new double[numClasses];
		
		

		for(int t = 1; t != iter; t++) {
			Arrays.fill(max_pg, Double.NEGATIVE_INFINITY);
			Arrays.fill(min_pg, Double.POSITIVE_INFINITY);

			long iter_time = System.currentTimeMillis();

			if(threshold > 0 || active <= threshold)
				ArrayUtils.shuffle(index, active);
			else
				Arrays.sort(index, 0, active);


			for(int j = 0; j != active; j++) {
				iters++;

				int i = index[j];


				int cls = i % numClasses;
				int x = i / numClasses;


				double alpha = alphas[i]; 
				double target = target(cls, x);
				double d_ii = norm == 2? 0.5/c : 0;
				double g = 0;

				g = dot(cls, x);

				g *= target;
				g -= 1;
				g += alpha*d_ii;

				boolean shrink = false;

				double c = norm == 1? this.c : Double.POSITIVE_INFINITY;

				double pg = g; // projected gradient
				if(alpha == 0) {
					pg = Math.min(g, 0.0);
					shrink = g > max_pg_pos[cls];
				} else if(alpha == c) {
					pg = Math.max(g, 0);
					shrink = g < min_pg_neg[cls];
				}

				min_pg[cls] = Math.min(min_pg[cls], pg);
				max_pg[cls] = Math.max(max_pg[cls], pg);

				if(shrink) {
					active--;
					ArrayUtils.swap(index, j, active);
					j--;
					continue;
				}

				if(pg != 0.0) {
					double alpha_old = alpha;
					double q = snorms[x] + d_ii;
					double alpha_new = Math.min(Math.max(alpha - g/q, 0.0), c);
					alphas[i] = alpha_new;
					double f = target*(alpha_new - alpha_old);
					add(cls, x, f);
				}

			}

			double diff =  Doubles.max(max_pg) - Doubles.min(min_pg);
			if(verbosity > 1)
				System.out.printf("Iter %d: active: %d\t eps=%.4f\t elapsed: %d msecs\n", t, active, diff, 
						System.currentTimeMillis() - iter_time);




			if(diff <= eps && active == samples) {
				if(verbosity > 0) {
					System.out.printf("Reached min eps at: %d, eps: %.4f%n", t, diff);
				}
				break;
			} else if(diff <= eps) {
				if(verbosity > 2) {
					System.out.print('*');
					System.out.flush();
				}
				active = samples;
				Arrays.fill(max_pg_pos, Double.POSITIVE_INFINITY);
				Arrays.fill(min_pg_neg, Double.NEGATIVE_INFINITY);
				continue; // perform full gradient check on next iteration
			}

			System.arraycopy(max_pg, 0, max_pg_pos, 0, max_pg.length);

			for(int i = 0; i < numClasses; i++) {
				if(max_pg[i] <= 0)
					max_pg_pos[i] = Double.POSITIVE_INFINITY;
			}

			System.arraycopy(min_pg, 0, min_pg_neg, 0, min_pg.length);

			for(int i = 0; i < numClasses; i++) {
				if(min_pg[i] >= 0) // m must be strictly negative
					min_pg_neg[i] = Double.NEGATIVE_INFINITY;

			}
		}

		elapsed = System.currentTimeMillis() - elapsed;

		if(verbosity > 0){
			System.out.printf("Optimization done in: %.2f secs%n", elapsed / 1.0E3);
			System.out.printf("Total evals: %d%n", iters);
			System.out.println("Done.");
		}
	}

}
