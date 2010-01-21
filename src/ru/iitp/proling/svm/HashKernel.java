package ru.iitp.proling.svm;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class HashKernel extends Kernel {
	protected Kernel base;
	protected int n;
	
	public HashKernel(Kernel base, int n){
		this.base = base;
		this.n = n;
	}

	@Override
	public void add(double[] dense, SparseVector x, double factor) {
		// TODO Auto-generated method stub
		base.add(dense, x, factor);

	}

	@Override
	public int dim(int srcDim) {
		// TODO Auto-generated method stub
		return n;
	}

	@Override
	public double dot(SparseVector x, SparseVector y) {
		// TODO Auto-generated method stub
		return base.dot(x, y);
	}

	@Override
	public double dot(double[] dense, SparseVector x) {
		// TODO Auto-generated method stub
		return base.dot(dense, x);
	}

	@Override
	public TreeMap<Long, Double> pipe(SparseVector x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double snorm(SparseVector x) {
		// TODO Auto-generated method stub
		TreeMap<Long, Double> v = base.pipe(x);
		
		double sum = 0; 
		
		Collection<Double> c = v.values();
		Iterator<Double> itr = c.iterator();
		while(itr.hasNext()){
			double val = itr.next();
			sum += val * val;
		}	
			
			
		
		
		return sum;
	}

}
