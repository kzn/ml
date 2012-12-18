package ru.iitp.proling.ml.scorer;

import java.util.Arrays;

import name.kazennikov.ml.core.Instance;

import cc.mallet.types.SparseVector;

import ru.iitp.proling.svm.kernel.Kernel;

/**
 * Trivial scorer. The score is k(w, x)
 * @author ant
 *
 */
public class TrivialScorer extends Scorer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double[] model;
	Kernel kernel;
	
	public TrivialScorer(double[] model, Kernel kernel){
		this.model = Arrays.copyOf(model, model.length);
		this.kernel = kernel;
	}
	

	@Override
	public double score(Instance v) {
		return kernel.dot(model, v);
	}


	@Override
	public double score(SparseVector v) {
		return kernel.dot(model, v);
	}
	
	

}
