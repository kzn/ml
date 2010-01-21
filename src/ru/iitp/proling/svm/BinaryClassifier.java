package ru.iitp.proling.svm;

import ru.iitp.proling.svm.kernel.Kernel;

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


	int classify(SparseVector v) {
		return kernel.dot(w, v) > 0.0? positive : negative;
	}
	
	double score(SparseVector v){
		return kernel.dot(w, v);
	}


}
