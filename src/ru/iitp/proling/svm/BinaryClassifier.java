package ru.iitp.proling.svm;

import ru.iitp.proling.svm.kernel.Kernel;

/**
 * Binary classification common class. Basic interface to binary predictor that has 2 outcomes.
 * This a indirect interface, as any binary classfier expected to conform to defined functions
 * @author ant
 *
 */
public class BinaryClassifier{
	protected double[] w;
	protected Kernel kernel;
	int positive;
	int negative;
	
	public BinaryClassifier(double[] w, Kernel kernel, int positive, int negative){
		this.w = w;
		this.kernel = kernel;
		this.positive = positive;
		this.negative = negative;
	}


	/**
	 * Classify vector
	 * @param v vector
	 * @return class of vector, -1 if negative in case of indicator classification
	 */
	int classify(SparseVector v) {
		return kernel.dot(w, v) > 0.0? positive : negative;
	}
	
	/**
	 * Get score of vector v, by this classifier. Can be used for various generalization performance measures.
	 * @param v vector
	 * @return score 
	 */
	double score(SparseVector v){
		return kernel.dot(w, v);
	}


}
