package ru.iitp.proling.svm;

import java.util.Arrays;
import java.util.HashSet;

import ru.iitp.proling.common.ArrayUtils;
import ru.iitp.proling.svm.kernel.Kernel;

public class MulticlassCS {

	protected Dataset ds; ///< Source dataset
	protected double[] alpha; ///< Alphas array
	protected double[] b; ///< 
	protected double[] g; ///< Gradient vector
	protected int[] alpha_index;
	protected double c;
	protected double eps;
	protected int n_classes;
	protected double[] snorms;
	protected Kernel kernel;
	
	protected int[] targets;
	
	protected double[] w;
	
	protected int n_features;
	protected int maxiter;

	public MulticlassCS(Dataset ds, int[] targets, double c, double eps, int maxiter, Kernel k){
		
		assert(targets.length == ds.size());
		this.targets = new int[targets.length];
		this.eps = eps;
		this.maxiter = maxiter;
		this.ds = ds;
		kernel = k;
		snorms = new double[ds.size()];
		
		for(int i = 0; i != snorms.length; i++)
			snorms[i] = kernel.snorm(ds.vec(i));
			
		
		// compute number of classes
		HashSet<Integer> hs = new HashSet<Integer>();
		
		for(int i = 0; i != targets.length; i++)
			this.targets[i] = targets[i] - 1;
		
		for(int t : targets)
			hs.add(t);
		
		n_classes = hs.size();
		
		System.out.printf("Found: %d classes\n", n_classes);
		
		
		alpha = new double[ds.size() * n_classes];
		b = new double[n_classes];
		g = new double[n_classes];
		n_features = kernel.dim(ds.max_dim()) + 1;
		w = new double[n_classes * n_features];
		
		
		alpha_index = new int[n_classes * ds.size()]; //alpha_index[m][i] = m initially
		this.c = c;
		
		
	};


	protected void w_add(int w_idx, int idx, double val){
		w[n_classes*idx + w_idx] += val;
		
	}
	
	protected double w(int w_idx, int idx){
		return w[n_classes*idx + w_idx];
	}

	protected void solve_subproblem(double A_i, int yi, double C_yi, int active_i, double[] alpha_new){
		int  r;
		double[] D = new double[active_i];
		for(int i = 0; i < active_i; i++)
			D[i] = b[i];

		if(yi < active_i)
			D[yi] += A_i*C_yi;

		Arrays.sort(D);
		double[] D1 = new double[active_i];
		
		for(int i = 0; i != D1.length; i++)
			D1[i] = D[D.length - 1 - i];
		
		D = D1;

		double beta = D[0] - A_i*C_yi;
		for(r = 1; r < active_i && beta < r*D[r]; r++)
			beta += D[r];

		beta /= r;
		for(r = 0; r < active_i; r++){
			if(r == yi)
				alpha_new[r] = Math.min(C_yi, (beta - b[r])/A_i);
			else
				alpha_new[r] = Math.min(0.0, (beta - b[r])/A_i);
		}
	}

	protected boolean be_shrunken(int m, int yi, double alpha_i, double minG){
		double bound = 0;
		if(m == yi)
			bound = c;
			if(alpha_i == bound && g[m] < minG)
				return true;
			return false;
	}


	/**
	     Add to g[m] dot(alpha_index_i[m]), for m < active_size
	     @param g - gradient vector
	     @param idx - sample index
	     @param active_size - number of active classes
	     @param alpha_index_i - indexes of active classes
	 */
	protected void grad_add_dot(double[] g, int idx, int active_size, int cls){
		
		SparseVector vec = ds.vec(idx);

		for(int i = 0; i != vec.size(); i++){
			for(int m = 0; m < active_size; m++)
				g[m] += w(get_alpha_index(cls, m), vec.indexes[i]) * vec.values[i];
		}
	}
	/**
	     Compute <w_idx, v_idx>
	 */
	double dot(int w_idx, int v_idx){
		double sum = 0;

		SparseVector vec = ds.vec(v_idx);

		for(int i = 0; i != vec.size(); i++){
			sum += w(w_idx, vec.indexes[i]) * vec.values[i];
		}

		return sum;
	}

	/**
	     Predict class of \a v_idx. Result is unmapped class.

	     If original class label is needed, one should map
	     the result value through classes.
	 */
	protected int predict_class(int v_idx){
		int t = -1;
		double max_score = Double.NEGATIVE_INFINITY;

		for(int i = 0; i != n_classes; i++){
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
		for(int i = 0; i != ds.size(); i++){
			if(predict_class(i) != targets[i])
				_loss++;
		}
		return 1.0*_loss/ds.size();
	}

	/**
	     Add alphas \a vec_idx vector multiplied by \a delta vector
	     @param vec_idx - sample idx
	     @param delta - array of <w_idx, factor>
	     @param delta_size - size of array \a delta
	 */
	protected void add_alphas(int vec_idx, int[] delta_idx, double[] delta_vals){
		SparseVector vec = ds.vec(vec_idx);
		for(int i = 0; i != vec.size(); i++){
			for(int j = 0; j != delta_idx.length; j++)
				w_add(delta_idx[j], vec.indexes[i], delta_vals[j]*vec.values[j]);
		}
	}




	public void solve(){
		System.out.printf("Dataset size: %d\n", ds.size());
		System.out.printf("Epsilon:%f\n", eps);
		
		double[] alpha_new = new double[n_classes]; // new \alpha
		int[] index = new int[ds.size()];
		
		
		int[] y_index = new int[ds.size()]; // y[i] -- целевые классы
		int active_size = ds.size();
		int[] active_size_i = new int[ds.size()]; // 
		double eps_shrink = Math.max(10.0*eps, 1.0); // stopping tolerance for shrinking
		boolean full_iteration = true;


		// optimized storage of delta(alpha_i) - for optimized update of
		// weight vector. stored in pairs of idx:value
		//uint32_t *delta_idx = new uint32_t[ds->size()];
		//double delta_val = new double[ds->size()];
		//pair<uint32_t, double> *delta = new pair<uint32_t, double>[ds->size()];
		int delta_size = 0;


		// initialization
		for(int i = 0; i != ds.size(); i++){
			for(int m = 0; m != n_classes; m++)
				alpha_index[i*n_classes + m] = m; //init alpha[i]

			active_size_i[i] = n_classes;
			y_index[i] = targets[i]; 
			index[i] = i;
		}

		for(int t = 0; t !=  maxiter; t++){

			double stopping = Double.NEGATIVE_INFINITY;
			
			ArrayUtils.shuffle(index, active_size); //FIXME: import shuffle

			System.out.printf("Iteration: %d active_size:%d\n", t, active_size);
			for(int j = 0; j != active_size; j++){
				int i = index[j];
				double snorm = kernel.snorm(ds.vec(i)); 
				//double *alpha_i = &alpha[i*model->n_classes]; // alpha_i -- массив размеров nr_class, в нем хранятся alpha[i] для каждого из классов
				//double[] alpha_i = 
				//uint32_t *alpha_index_i = &alpha_index[i*model->n_classes];

				if(snorm > 0){ // skip zero vectors

					// active_size_i[i] -- массив рамером l, в котором хранится последний(максимальный) активный класс
					for(int m = 0; m < active_size_i[i]; m++) 
						g[m] = 1;

					// G[y_i] = 0, если y_i - активен
					if(y_index[i] < active_size_i[i])
						g[y_index[i]] = 0;

					grad_add_dot(g, i, active_size_i[i], i);


					double minG = Double.POSITIVE_INFINITY;
					double maxG = Double.NEGATIVE_INFINITY;

					// track max positive G, and min negative G
					for(int m = 0; m < active_size_i[i]; m++){
						if(alpha(i, get_alpha_index(i, m)) < 0 && g[m] < minG)
							minG = g[m];
						if(g[m] > maxG)
							maxG = g[m];
					}

					// если целевое значение активно, и если 
					if(y_index[i] < active_size_i[i])
						if(alpha(i, targets[i]) < c && g[y_index[i]] < minG)
							minG = g[y_index[i]];

					for(int m = 0; m < active_size_i[i]; m++){ // Needed, when active_size_i[i] = 0
						if(be_shrunken(m, y_index[i], alpha(i, get_alpha_index(i, m)), minG)){
							active_size_i[i]--;
							while(active_size_i[i] > m){
								if(!be_shrunken(active_size_i[i], y_index[i],
										alpha(i, get_alpha_index(i, active_size_i[i])), minG)){
									ArrayUtils.swap(alpha_index, n_classes * i + m, n_classes * i + active_size_i[i]);
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
						b[m] = g[m] - snorm*alpha(i, get_alpha_index(i, m));

					solve_subproblem(snorm, y_index[i], c, active_size_i[i], alpha_new);

					// add alpha
					delta_size = 0;
					for(int	m = 0; m != active_size_i[i]; m++){
						double dalpha = alpha_new[m] - alpha(i, get_alpha_index(i, m));
						set_alpha(i, get_alpha_index(i, m), alpha_new[m]);

						if(Math.abs(dalpha) >= 1e-12){ // is it worth to add to weight vector?
							add(get_alpha_index(i, m), i, dalpha);
						}
					}
				}
			}

			if(stopping < eps_shrink){

				if(stopping < eps && full_iteration == true){
					System.out.printf("Stopped at:%d iteration\n",t);
					break;
				}
				// reactivate all samples and classes
				active_size = ds.size();
				for(int k = 0; k < ds.size(); k++)
					active_size_i[k] = n_classes;

					System.out.print('*');
					System.out.flush();
					eps_shrink = Math.max(eps_shrink/2, eps);
					full_iteration = true;
					continue;
			}
			full_iteration = false;
		}

		

	};
	
	protected int get_alpha_index(int i, int sample){
		return alpha_index[i*n_classes + sample];
	}
	
	protected void set_alpha_index(int i, int sample, int val){
		alpha_index[i*n_classes + sample] = val;
	}
	
	protected double alpha(int i, int cls){
		return alpha[i*n_classes + cls];
	}
	
	protected void set_alpha(int i, int cls, double val){
		alpha[i* n_classes + cls] = val;
	}

	public double dual_objective(){
		double obj = 0;
		for(int i = 0; i != n_classes; i++)
			obj += snorms[i];
		System.out.println("Norms:" + obj);

		obj = 0.5 * obj;

		for(int i = 0; i < ds.size()*n_classes; i++)
			obj += alpha[i];
		System.out.println("Norms + alpha:" + obj);

		for(int i = 0; i != ds.size(); i++)
			obj -= alpha[i*n_classes + targets[i]];

		return obj;
	};

	/**
	     Add \a factor * \a vec_idx to \a w_idx vector
	     @param w_idx index of target weight vector
	     @param vec_idx index of sample
	     @param factor factor coefficient
	 */
	void add(int w_idx, int vec_idx, double factor){
		
		SparseVector vec = ds.vec(vec_idx);

		for(int i = 0; i != vec.size(); i++){
			w_add(w_idx, vec.indexes[i],factor*vec.values[i]);
		}
	}



}
