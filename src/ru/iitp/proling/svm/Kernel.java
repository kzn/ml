package ru.iitp.proling.svm;

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
	public abstract SparseVector pipe(SparseVector x); 
}
