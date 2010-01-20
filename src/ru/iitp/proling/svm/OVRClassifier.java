package ru.iitp.proling.svm;

import java.util.SortedMap;

/**
 * One vs. Rest classifier.
 * @author ant
 *
 */

public class OVRClassifier extends Classifier {
	protected Alphabet alphabet;
	protected double[] w;
	protected Kernel kernel;
	int positive;
	
	public OVRClassifier(double[] w, Kernel kernel, Alphabet alphabet, int positive){
		this.alphabet = alphabet;
		this.w = w;
		this.kernel = kernel;
		this.positive = positive;
		
	}

	@Override
	Alphabet alphabet() {
		// TODO Auto-generated method stub
		return alphabet;
	}

	@Override
	int classify(SparseVector v) {
		// TODO Auto-generated method stub
		return kernel.dot(w, v) > 0.0? positive : 0;
	}

	@Override
	SortedMap<Double, Integer> scores() {
		// TODO Auto-generated method stub
		return null;
	}

}

