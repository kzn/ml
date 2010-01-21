package ru.iitp.proling.svm.kernel;

import java.util.TreeMap;

import ru.iitp.proling.svm.SparseVector;

/*
 * Abstract class instead of Interface for efficiency reasons
 */
public abstract class Kernel {
	protected wAccessor wa;
	public Kernel(){
		wa = new wAccessor();
	}
	public abstract double dot(SparseVector x, SparseVector y);
	public abstract double dot(double[] dense, SparseVector x);
	public abstract void add(double[] dense, SparseVector x, double factor);
	public abstract int dim(int src_dim);
	public abstract double snorm(SparseVector x);
	// perform transformation of input sparse vector
	public abstract TreeMap<Long, Double> pipe(SparseVector x);
	
	public void wAdder(double[] dense, long idx, double value) {
		wa.wAdder(dense, idx, value);
	
	}
	
	public double wGetter(double[] dense, long idx) {
		return wa.wGetter(dense, idx);
	}


	public void wSetter(double[] dense, long idx, double value) {
		wa.wSetter(dense, idx, value);
	}
	
	
	public void setwa(wAccessor wa){
		this.wa = wa;
	}


}
