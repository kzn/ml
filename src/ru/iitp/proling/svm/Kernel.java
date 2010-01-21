package ru.iitp.proling.svm;

import java.util.TreeMap;

/*
 * Abstract class instead of Interface for efficiency reasons
 */
public abstract class Kernel {
	public abstract double dot(SparseVector x, SparseVector y);
	public abstract double dot(double[] dense, SparseVector x);
	public abstract void add(double[] dense, SparseVector x, double factor);
	public abstract int dim(int src_dim);
	public abstract double snorm(SparseVector x);
	// perform transformation of input sparse vector
	public abstract TreeMap<Long, Double> pipe(SparseVector x); 
	

	public void wAdder(double[] dense, long idx, double value) {
		dense[(int) idx] += value;
	}


	public double wGetter(double[] dense, long idx) {
		return dense[(int) idx];
	}


	public void wSetter(double[] dense, long idx, double value) {
		dense[(int)idx] = value;
	}
}
