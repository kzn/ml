package ru.iitp.proling.ml.svm;

import java.util.Arrays;

import ru.iitp.proling.common.ArrayUtils;

public abstract class AbstractMulticlassCS {
	double c;
	double eps;
	int maxiter;
	protected double[] alpha; ///< Alphas array
	protected double[] b; ///< 
	protected double[] g; ///< Gradient vector
	protected int[] alpha_index;

	public AbstractMulticlassCS(double c, double eps, int maxiter){
		this.c = c;
		this.eps = eps;
		this.maxiter = maxiter;
	}
	
	public void init() {
		alpha = new double[size() * classes()];
		b = new double[classes()];
		g = new double[classes()];
		alpha_index = new int[classes() * size()]; //alpha_index[m][i] = m initially
	}


	public abstract int size();
	public abstract int classes();

	/**
	 * Add factor*vec_idx to w_idx weight vector
	 * @param w_idx index of target weight vector
	 * @param vec_idx index of sample
	 * @param factor factor coefficient
	 */
	public abstract void add(int w_idx, int vec_idx, double factor);

	/**
	 *  Compute <w_idx, v_idx>
	 */
	public abstract double dot(int w_idx, int v_idx);
	public abstract int target(int vec);
	public abstract double snorm(int vec);
	






	protected double get_alpha(int sample, int cls){
		return alpha[sample * classes() + cls];
	}

	protected void set_alpha(int sample, int cls, double value){
		alpha[sample * classes() + cls] = value;
	}

	protected int get_alpha_idx(int sample, int cls){
		return alpha_index[sample * classes() + cls];
	}

	protected void set_alpha_idx(int sample, int cls, int value){
		alpha_index[sample * classes() + cls] = value;
	}

	protected void solve_subproblem(double A_i, int yi, double C_yi, int active_i, double[] alpha_new){
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

	protected boolean be_shrunken(int m, int yi, double alpha_i, double minG){
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
	protected int predict_class(int v_idx){
		int t = -1;
		double max_score = Double.NEGATIVE_INFINITY;

		for(int i = 0; i != classes(); i++){
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
		
		for(int i = 0; i != size(); i++){
			if(predict_class(i) != target(i))
				_loss++;
		}
		
		System.out.printf("Zero-one itermediate: %d/%d\n",_loss, size());
		return 1.0*_loss/size();
	}


	public void solve(){
		init();
		System.out.println("Dataset size:" + size());
		double[] alpha_new = new double[classes()]; // new \alpha
		int[] index = new int[size()]; // array of indexes of samples

		int[] y_index = new int[size()]; // y[i] -- целевые классы
		int active_size = size();
		int[] active_size_i = new int[size()]; // 
		double eps_shrink = Math.max(10.0*eps, 1.0); // stopping tolerance for shrinking
		boolean full_iteration = true;

		long start_point = System.nanoTime();
		// initialization
		for(int i = 0; i != size(); i++){

			for(int m = 0; m != classes(); m++)
				alpha_index[i*classes() + m] = m; //init alpha[i]

			active_size_i[i] = classes();
			y_index[i] = target(i); 
			index[i] = i;
		}

		for(int t = 0; t !=  maxiter; t++){

			double stopping = Double.NEGATIVE_INFINITY;
			long iter_start = System.nanoTime();
			ArrayUtils.shuffle(index, active_size);

			for(int j = 0; j != active_size; j++){
				int i = index[j];
				double snorm = snorm(i); 

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
						if(get_alpha(i, target(i)) < c && g[y_index[i]] < minG)
							minG = g[y_index[i]];

					for(int m = 0; m < active_size_i[i]; m++){ // Needed, when active_size_i[i] = 0
						if(be_shrunken(m, y_index[i], get_alpha(i, get_alpha_idx(i, m)), minG)){
							active_size_i[i]--;
							while(active_size_i[i] > m){
								if(!be_shrunken(active_size_i[i], y_index[i],
										get_alpha(i, get_alpha_idx(i, active_size_i[i])), minG)){
									ArrayUtils.swap(alpha_index, i*classes() + m, i*classes() + active_size_i[i]);
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
				active_size = size();
				for(int k = 0; k < size(); k++)
					active_size_i[k] = classes();

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
