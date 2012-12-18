package name.kazennikov.ml.svm;

import java.util.List;

import name.kazennikov.ml.core.DatasetUtils;
import name.kazennikov.ml.core.Instance;


/**
 * Simple DCD Linear SVM solver for dataset from {@link Instance} class
 * @author Anton Kazennikov
 *
 */
public class DCDLinear extends AbstractDCD {
	
	List<Instance> dataset;
	double w[];
	double targets[];
	
	protected DCDLinear(double c_pos, double c_neg, int iter, double eps, int threshold) {
		super(c_pos, c_neg, iter, eps, threshold);
	}
	
	/**
	 * Return current value of the weight vector
	 */
	public double[] w() {
		return w;
	}

	/**
	 * Construct solver
	 * @param dataset sample dataset
	 * @param targets sample targe values
	 * @param c_pos C for positive samples
	 * @param c_neg C for negative examples
	 * @param iter max number of iterations to perform
	 * @param eps epsilon to stop within optimal solution
	 * @param threshold active vars threshold between shuffling and sorting
	 */
	public DCDLinear(List<Instance> dataset, double[] targets, double c_pos, double c_neg, 
			int iter, double eps, int threshold) {
		super(c_pos, c_neg, iter, eps, threshold);
		this.dataset = dataset;
		this.targets = targets;
		w = new double[DatasetUtils.dim(dataset) + 1];
	}
	

	@Override
	public void init() {
		for(int i = 0; i != w.length; i++) {
			w[i] = 0;
		}
	}

	@Override
	public double snorm(int vec) {
		Instance v = dataset.get(vec);
		double snorm = 0;
		for(int i = 0; i != v.size(); i++) {
			snorm += v.valueAt(i) * v.valueAt(i);
		}

		return snorm;
	}

	@Override
	public int size() {
		return dataset.size();
	}

	@Override
	public double dot(int vec) {
		Instance v = dataset.get(vec);
		double val = 0;
		
		for(int i = 0; i != v.size(); i++) {
			val += w[v.indexAt(i)] * v.valueAt(i);
		}

		return val;
	}

	@Override
	public void add(int vec, double factor) {
		Instance v = dataset.get(vec);
		
		for(int i = 0; i != v.size(); i++) {
			w[v.indexAt(i)] += factor * v.valueAt(i);
		}

	}

	@Override
	public double target(int vec) {
		return targets[vec];
	}

}
