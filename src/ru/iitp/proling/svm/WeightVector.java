package ru.iitp.proling.svm;

public abstract class WeightVector {
	
	// direct wv properties
	public abstract int dim(); // dimension of model
	public abstract double norm();
	public abstract double snorm();
	
	
	
	// problem properties
	public abstract int size(); // problem size
	public abstract void add(int idx, double factor);
	
	public abstract void add_alpha(int idx, double factor);
	
	public abstract double alpha(int idx);
	
	public abstract double target(int idx);
	public abstract double dot(int idx);
	public abstract double dot(int x, int y);
	public abstract double snorm(int idx);
}
