package ru.iitp.proling.ml.svm;

import java.util.List;

import ru.iitp.proling.ml.core.Instance;

/**
 * Linear DCD extended by Hash Kernels
 * @author Anton Kazennikov
 *
 */
public abstract class DCDLinearHK extends DCDLinear {

	/**
	 * Comstruct DCD Linear SVM solver
	 * @param dataset sample dataset
	 * @param targets sample target values
	 * @param dim dimensionality of the target space
	 * @param c_pos positive C for positive examples
	 * @param c_neg C for negative examples
	 * @param iter maximum iterations
	 * @param eps epsilon to optimal solution
	 * @param threshold threshold for active vars for selecting shuffling or sorting
	 */
	public DCDLinearHK(List<Instance> dataset, double[] targets, int dim,
			double c_pos, double c_neg, int iter, double eps, int threshold) {
		super(c_pos, c_neg, iter, eps, threshold);
		this.dataset = dataset;
		this.targets = targets;
		this.w = new double[dim];
	}
	
	/**
	 * Compute hashed coordinate for given source coordinate
	 * @param x source coordinate
	 * @return hashed coordinate
	 */
	public abstract int h(int x);

	/* (non-Javadoc)
	 * @see ru.iitp.proling.ml.svm.DCDLinear#dot(int)
	 */
	@Override
	public double dot(int vec) {
		Instance v = dataset.get(vec);
		double val = 0;
		for(int i = 0; i != v.size(); i++) {
			val += w[h(v.indexAt(i))] * v.valueAt(i);
		}
		return val;
	}

	/* (non-Javadoc)
	 * @see ru.iitp.proling.ml.svm.DCDLinear#add(int, double)
	 */
	@Override
	public void add(int vec, double factor) {
		Instance v = dataset.get(vec);
		for(int i = 0; i != v.size(); i++) {
			w[h(v.indexAt(i))] += factor * v.valueAt(i);
		}
	}
}
