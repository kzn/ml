package ru.iitp.proling.svm;

/**
 * Simple binary classifier class
 * @author ant
 *
 */

public class SimpleClassifier {
	protected Kernel kernel;
	protected double[] w;
	
	public SimpleClassifier(double[] w, Kernel kernel){
		this.kernel = kernel;
		this.w = w;
	}
	
	public double score(SparseVector vec){
		return kernel.dot(w, vec);
	}
	
	public double classify(SparseVector vec){
		return score(vec) > 0? 1 : -1;
	}

}
