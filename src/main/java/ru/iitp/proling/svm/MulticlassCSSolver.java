package ru.iitp.proling.svm;

import java.util.Arrays;

import ru.iitp.proling.common.ArrayUtils;
import ru.iitp.proling.common.MurmurHash;
import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.MultiScorer;
import ru.iitp.proling.ml.core.MulticlassProblem;
import ru.iitp.proling.ml.core.MulticlassSolver;
import ru.iitp.proling.svm.kernel.Kernel;
import ru.iitp.proling.svm.kernel.LinearKernel;

public class MulticlassCSSolver implements MulticlassSolver {
	double c;
	double eps;
	int maxiter;

	public MulticlassCSSolver(double c, double eps, int maxiter){
		this.c = c;
		this.eps = eps;
		this.maxiter = maxiter;
	}

	@Override
	public SimpleMultiScorer solve(MulticlassProblem problem){
//		if(!(problem.kernel() instanceof LinearKernel))
//			throw new IllegalArgumentException("Crammer & Singer Multiclass optimization can work only with linear kernel");

		SimpleMulticlassCS mc = new SimpleMulticlassCS(problem, c, eps, maxiter);
		mc.solve();
		
		return null;//new SimpleMultiScorer(mc.w, true, problem.dim(), problem.classes(), problem.kernel());
		
	}

	public static abstract class AbstractMulticlassCS {
		MulticlassProblem problem;
		protected double[] alpha; ///< Alphas array
		protected double[] b; ///< 
		protected double[] g; ///< Gradient vector
		protected int[] alpha_index;
		double c;
		double eps;
		int maxIter;
		int n_classes;
	

		public AbstractMulticlassCS(MulticlassProblem problem, double c, double eps, int maxIter){
			this.problem = problem;
			this.c = c;
			this.eps = eps;
			this.maxIter = maxIter;
			alpha = new double[problem.size() * problem.classes()];
			b = new double[problem.classes()];
			g = new double[problem.classes()];
			n_classes  = problem.classes();
			alpha_index = new int[problem.classes() * problem.size()]; //alpha_index[m][i] = m initially
		}
		
		/**
	     Add factor*vec_idx to w_idx weight vector
	     @param w_idx index of target weight vector
	     @param vec_idx index of sample
	     @param factor factor coefficient
		 */
		abstract void add(int w_idx, int vec_idx, double factor);
		
		/**
	     Compute <w_idx, v_idx>
		 */
		abstract double dot(int w_idx, int v_idx);





		
		double get_alpha(int sample, int cls){
			return alpha[sample * n_classes + cls];
		}

		void set_alpha(int sample, int cls, double value){
			alpha[sample * n_classes + cls] = value;
		}

		int get_alpha_idx(int sample, int cls){
			return alpha_index[sample*problem.classes() + cls];
		}

		void set_alpha_idx(int sample, int cls, int value){
			alpha_index[sample*problem.classes() + cls] = value;
		}

		void solve_subproblem(double A_i, int yi, double C_yi, int active_i, double[] alpha_new){
			int r;
			double[] D = new double[active_i];
			for(int i = 0; i < active_i; i++)
				D[i] = b[i];

			if(yi < active_i)
				D[yi] += A_i*C_yi;


			Arrays.sort(D); // reverse order

			double beta = D[D.length - 1] - A_i*C_yi;
			for(r = 1; r < active_i && beta < r*D[D.length - 1 - r]; r++)
				beta += D[D.length - 1 - r];

			beta /= r;
			for(r = 0; r < active_i; r++){
				if(r == yi)
					alpha_new[r] = Math.min(C_yi, (beta - b[r])/A_i);
				else
					alpha_new[r] = Math.min(0.0, (beta - b[r])/A_i);
			}
		}

		boolean be_shrunken(int m, int yi, double alpha_i, double minG){
			double bound = 0;
			if(m == yi)
				bound = c;
			if(alpha_i == bound && g[m] < minG)
				return true;
			return false;
		}


		/**
	     Add to g[m] dot(), for m < active_size
	     @param g - gradient vector
	     @param idx - sample index
	     @param active_size - number of active classes
		 */
		void grad_add_dot(double[] g, int idx, int active_size){
			for(int m = 0; m < active_size; m++) {
				g[m] += dot(get_alpha_idx(idx, m), idx);
			}
		}

		
		
		/**
	     Predict class of \a v_idx. Result is unmapped class.

	     If original class label is needed, one should map
	     the result value through classes.
		 */
		int predict_class(int v_idx){
			int t = -1;
			double max_score = Double.NEGATIVE_INFINITY;

			for(int i = 0; i != problem.classes(); i++){
				double score = dot(i, v_idx);
				if(score > max_score){
					t = i;
					max_score = score;
				}
			}

			return t;
		}

		public double zero_one_loss(){
			int _loss = 0;
			for(int i = 0; i != problem.size(); i++){
				if(predict_class(i) != problem.target(i))
					_loss++;
			}
			System.out.printf("Zero-one itermediate: %d/%d\n",_loss, problem.size());
			return 1.0*_loss/problem.size();
		}


		public void solve(){
			System.out.println("Dataset size:" + problem.size());
			double[] alpha_new = new double[problem.classes()]; // new \alpha
			int[] index = new int[problem.size()]; // array of indexes of samples

			int[] y_index = new int[problem.size()]; // y[i] -- целевые классы
			int active_size = problem.size();
			int[] active_size_i = new int[problem.size()]; // 
			double eps_shrink = Math.max(10.0*eps, 1.0); // stopping tolerance for shrinking
			boolean full_iteration = true;

			long start_point = System.nanoTime();
			// initialization
			for(int i = 0; i != problem.size(); i++){

				for(int m = 0; m != problem.classes(); m++)
					alpha_index[i*problem.classes() + m] = m; //init alpha[i]

				active_size_i[i] = problem.classes();
				y_index[i] = problem.target(i); 
				index[i] = i;
			}

			for(int t = 0; t !=  maxIter; t++){

				double stopping = Double.NEGATIVE_INFINITY;
				long iter_start = System.nanoTime();
				ArrayUtils.shuffle(index, active_size);

				for(int j = 0; j != active_size; j++){
					int i = index[j];
					double snorm = problem.snorm(i); 

					if(snorm > 0){ // skip zero vectors

						// active_size_i[i] -- массив рамером l, в котором хранится последний(максимальный) активный класс
						for(int m = 0; m < active_size_i[i]; m++) 
							g[m] = 1;

						// G[y_i] = 0, если y_i - активен
						if(y_index[i] < active_size_i[i])
							g[y_index[i]] = 0;

						grad_add_dot(g, i, active_size_i[i]);


						double minG = Double.POSITIVE_INFINITY;
						double maxG = Double.NEGATIVE_INFINITY;

						// track max positive G, and min negative G
						for(int m = 0; m < active_size_i[i]; m++){
							if(get_alpha(i, get_alpha_idx(i, m)) < 0 && g[m] < minG)
								minG = g[m];
							if(g[m] > maxG)
								maxG = g[m];
						}

						// если целевое значение активно, и если 
						if(y_index[i] < active_size_i[i])
							if(get_alpha(i, problem.target(i)) < c && g[y_index[i]] < minG)
								minG = g[y_index[i]];

						for(int m = 0; m < active_size_i[i]; m++){ // Needed, when active_size_i[i] = 0
							if(be_shrunken(m, y_index[i], get_alpha(i, get_alpha_idx(i, m)), minG)){
								active_size_i[i]--;
								while(active_size_i[i] > m){
									if(!be_shrunken(active_size_i[i], y_index[i],
											get_alpha(i, get_alpha_idx(i, active_size_i[i])), minG)){
										ArrayUtils.swap(alpha_index, i*problem.classes() + m, i*problem.classes() + active_size_i[i]);
										ArrayUtils.swap(g, m, active_size_i[i]);

										if(y_index[i] == active_size_i[i])
											y_index[i] = m;

										else if(y_index[i] == m)
											y_index[i] = active_size_i[i];
										break;
									}
									active_size_i[i]--;
								}
							}
						}

						if(active_size_i[i] <= 1){
							active_size--;
							ArrayUtils.swap(index, j, active_size);
							j--;
							continue;
						}

						if(maxG - minG <= 1e-12)
							continue;
						else
							stopping = Math.max(maxG - minG, stopping);

						for(int m = 0; m != active_size_i[i]; m++)
							b[m] = g[m] - snorm*get_alpha(i, get_alpha_idx(i,m));

						solve_subproblem(snorm, y_index[i], c, active_size_i[i], alpha_new);


						for(int m = 0; m != active_size_i[i]; m++){
							double dalpha = alpha_new[m] - get_alpha(i, get_alpha_idx(i, m));
							set_alpha(i, get_alpha_idx(i, m), alpha_new[m]);

							if(Math.abs(dalpha) >= 1e-12){ // is it worth to add to weight vector?
								add(get_alpha_idx(i, m), i, dalpha);
								// delta[delta_size].first = alpha_index_i[m];
								// delta[delta_size].second = dalpha;
								// delta_size++;
							}
						}

						//add_mcalpha(i, delta, delta_size);
					}
				}
				iter_start = System.nanoTime() - iter_start;
				System.out.printf("Iteration:%d active_size:%d eps=%f time=%f msecs\n", t, active_size, stopping, 1.0*iter_start/1E6);

				if(stopping < eps_shrink){

					if(stopping < eps && full_iteration == true){
						System.out.println("Stopped at:" + t);
						System.out.printf("Optimization done in: %f secs\n", 1.0*(System.nanoTime() - start_point)/1E9);
						break;
					}
					// reactivate all samples and classes
					active_size = problem.size();
					for(int k = 0; k < problem.size(); k++)
						active_size_i[k] = problem.classes();

					System.out.print('*');
					System.out.flush();
					eps_shrink = Math.max(eps_shrink/2, eps);
					full_iteration = true;
					continue;
				}
				full_iteration = false;
			}


		}
	}
	
	
	
	public static class SimpleMulticlassCS extends AbstractMulticlassCS {
		//protected Kernel kernel;
		int n_features;
		protected double[] w;
		
	

		public SimpleMulticlassCS(MulticlassProblem problem, double c, double eps, int maxIter){
			super(problem, c, eps, maxIter);
			//this.kernel = problem.kernel();
			n_features = problem.dim() + 1;//kernel.dim(problem.dim()) + 1;
			w = new double[n_features * n_classes];
		};


//		@Override
//		double w(int vec_idx, int value_idx){
//			return w[value_idx * n_classes + vec_idx];
//		}
//
//		@Override
//		void w_add(int vec_idx, int value_idx, double value){
//			w[value_idx * n_classes + vec_idx] += value;
//
//		}

		@Override
		double dot(int w_idx, int v_idx) {
			Instance vec = problem.get(v_idx);
			double sum = 0;

			for(int i = 0; i != vec.size(); i++){
				sum += w[v_idx * n_classes + w_idx] * vec.valueAt(i);
			}

			return sum;
		}


		public double dual_objective(){
			double obj = 0;
			for(int i = 0; i != problem.classes(); i++)
				obj += snorm(i);

			System.out.println("Norms:" + obj);

			obj = 0.5 * obj;

			for(int i = 0; i != problem.size(); i++)
				for(int j = 0; j != problem.classes(); j++)
					obj += get_alpha(i,j);
			System.out.println("Norms + alpha:" + obj);

			for(int i = 0; i != problem.size(); i++)
				obj -= get_alpha(i, problem.target(i));
			//cout<<"Norms + alpha - alpha_prob:"<<obj<<endl;
			System.out.println("dual objective:" + obj);

			return obj;
		};

		// compute squared norm of \a i-th weight vector
		double snorm(int idx){
			double sum = 0;
			for(int i = 0; i != n_features; i++) {
				double wcoord = w[i * n_classes + idx];
				sum += wcoord * wcoord;
			}
			return sum;
		}
		
		public double[] w() {
			return w;
		}


		@Override
		void add(int w_idx, int vec_idx, double factor) {
			Instance vec = problem.get(vec_idx);
			for(int i = 0; i != vec.size(); i++) {
				w[vec.indexAt(i) * n_classes + w_idx] += vec.valueAt(i) * factor;
			}
		}
	}
	

	public static abstract class AbstractHashMCCS extends AbstractMulticlassCS {
		double[] w;
		int dim;

		public AbstractHashMCCS(MulticlassProblem problem, int dim, double c, double eps, int maxIter) {
			super(problem, c, eps, maxIter);
			this.dim = dim;
			w = new double[dim];
		}
		
		
		
		@Override
		void add(int w_idx, int vec_idx, double factor) {
			Instance vec = problem.get(vec_idx);
			
			for(int i = 0; i != vec.size(); i++) {
				w[h(w_idx, vec.indexAt(i))] += vec.valueAt(i) * factor;
			}
		}
		
		@Override
		double dot(int w_idx, int v_idx) {
			Instance x = problem.get(v_idx);
			double val = 0;
			
			for(int i = 0; i != x.size(); i++) {
				val += w[h(w_idx, x.indexAt(i))] + x.valueAt(i);
			}

			return val;
		}


		
		public abstract int h(int v, int x);
		
		public int dim() {
			return dim;
		}
		
		public double[] w() {
			return w;
		}

		
	}
	
}
