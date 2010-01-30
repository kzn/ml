package ru.iitp.proling.svm;

import ru.iitp.proling.svm.kernel.Kernel;

public class TrivialScorer extends Scorer {
	double[] model;
	Kernel kernel;
	
	public TrivialScorer(double[] model, Kernel kernel){
		this.model = model;
		this.kernel = kernel;
	}
	

	@Override
	public double score(SparseVector v) {
		return kernel.dot(model, v);
	}

}
