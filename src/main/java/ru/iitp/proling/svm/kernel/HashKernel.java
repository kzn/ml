package ru.iitp.proling.svm.kernel;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import name.kazennikov.ml.core.Instance;


public abstract class HashKernel extends Kernel implements Serializable{
	protected Kernel base;
	protected int n;
	
	public HashKernel(Kernel base, int n){
		this.base = base;
		this.n = n;
		this.base.setwa(new wAccessorHash(n));
	}

	@Override
	public void add(double[] dense, Instance x, double factor) {
		// TODO Auto-generated method stub
		base.add(dense, x, factor);

	}

	@Override
	public int dim(int srcDim) {
		// TODO Auto-generated method stub
		return n;
	}

	@Override
	public double dot(Instance x, Instance y) {
		// TODO Auto-generated method stub
		return base.dot(x, y);
	}

	@Override
	public double dot(double[] dense, Instance x) {
		// TODO Auto-generated method stub
		return base.dot(dense, x);
	}

	@Override
	public TreeMap<Long, Double> pipe(Instance x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double snorm(Instance x) {
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
	// ###### Hash kernel ##### 
	


}
