package name.kazennikov.ml.svm;

import name.kazennikov.ml.core.Instance;
import name.kazennikov.ml.core.MulticlassProblem;


public abstract class AbstractHKMulticlassCS extends AbstractMulticlassCS {
	MulticlassProblem problem;
	double[] w;
	double[] snorms;
	int dim;


	public AbstractHKMulticlassCS(MulticlassProblem problem, int dim, double c, double eps, int maxiter) {
		super(c, eps, maxiter);
		this.problem = problem;
		this.dim = dim;
		this.w = new double[dim];
		this.snorms = new double[problem.size()];
		
		for(int i = 0; i != problem.size(); i++) {
			Instance inst = problem.get(i);
			for(int j = 0; j != inst.size(); j++) {
				this.snorms[i] += inst.valueAt(j) * inst.valueAt(j);
			}
		}
	}
	
	public abstract int h(int v, int x);

	@Override
	public int size() {
		return problem.size();
	}

	@Override
	public int classes() {
		return problem.classes();
	}

	@Override
	public int target(int vec) {
		return problem.target(vec);
	}

	@Override
	public double snorm(int vec) {
		return snorms[vec];
	}





	@Override
	public void add(int w_idx, int vec_idx, double factor) {
		Instance vec = problem.get(vec_idx);

		for(int i = 0; i != vec.size(); i++) {
			w[h(w_idx, vec.indexAt(i))] += vec.valueAt(i) * factor;
		}
	}

	@Override
	public double dot(int w_idx, int v_idx) {
		Instance x = problem.get(v_idx);
		double val = 0;

		for(int i = 0; i != x.size(); i++) {
			val += w[h(w_idx, x.indexAt(i))] + x.valueAt(i);
		}

		return val;
	}

	public int dim() {
		return dim;
	}

	public double[] w() {
		return w;
	}

}
