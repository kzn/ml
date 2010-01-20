package ru.iitp.proling.svm;

public class PolyKernel extends Kernel {
	protected double g;
	protected double c;
	protected int n;
	
	PolyKernel(double g, double c, int n){
		this.g = g;
		this.c = c;
		this.n = n;
	}

	@Override
	public int dim(int n) {
		return (n+2)*(n+1)/2;
	}

	@Override
	public double dot(SparseVector x, SparseVector y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double dot(double[] dense, SparseVector x) {
		// TODO Auto-generated method stub
		return 0;
	}

}
