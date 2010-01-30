package ru.iitp.proling.svm;

import java.util.Arrays;

import ru.iitp.proling.svm.kernel.Kernel;

public class TrivialScorer extends Scorer {
	double[] model;
	Kernel kernel;
	
	public TrivialScorer(double[] model, Kernel kernel){
		this.model = Arrays.copyOf(model, model.length);
		this.kernel = kernel;
	}
	

	@Override
	public double score(SparseVector v) {
		return kernel.dot(model, v);
	}

}
