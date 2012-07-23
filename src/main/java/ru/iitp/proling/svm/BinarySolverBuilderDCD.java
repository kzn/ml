package ru.iitp.proling.svm;

public class BinarySolverBuilderDCD extends BinarySolverBuilder {
	public double c_pos;
	public double c_neg;
	public int iter;
	public double eps;
	public int threshold;
	
	public BinarySolverBuilderDCD(double c_pos, double c_neg, int iter, double eps, int threshold){
		this.c_pos = c_pos;
		this.c_neg = c_neg;
		this.iter = iter;
		this.eps = eps;
		this.threshold = threshold;
		
	}

	@Override
	public BinarySolver create() {
		return new DCDSolver(c_pos, c_neg, iter, eps, threshold);
	}

}
