package ru.iitp.proling.svm;

import java.util.SortedMap;

public class BinaryClassifier extends Classifier {
	protected Alphabet alphabet;
	protected double[] w;
	protected Kernel kernel;
	int positive;
	int negative;
	
	public BinaryClassifier(double[] w, Kernel kernel, Alphabet alphabet, int positive, int negative){
		this.alphabet = alphabet;
		this.w = w;
		this.kernel = kernel;
		this.positive = positive;
		this.negative = negative;
		
	}

	@Override
	Alphabet alphabet() {
		// TODO Auto-generated method stub
		return alphabet;
	}

	@Override
	int classify(SparseVector v) {
		// TODO Auto-generated method stub
		return kernel.dot(w, v) > 0.0? positive : negative;
	}

	@Override
	SortedMap<Double, Integer> scores() {
		// TODO Auto-generated method stub
		return null;
	}

}
