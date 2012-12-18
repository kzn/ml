package ru.iitp.proling.svm.kernel;

import java.io.Serializable;
import java.util.TreeMap;

import name.kazennikov.ml.core.Instance;

import cc.mallet.types.SparseVector;


public class LinearKernel extends Kernel implements Serializable{

	@Override
	public double dot(Instance x, Instance y) {
		double res = 0;
		int j = 0;
		int k = 0;
		
		while(j != x.size()	 && k != y.size()){
			if(x.indexAt(j) < y.indexAt(k))
				j++;
			else if(x.indexAt(j) > y.indexAt(k))
				k++;
			else{
				res += x.valueAt(j) * y.valueAt(k);
				j++;
				k++;
			}
		}
		
		return res;

	}

	@Override
	public double dot(double[] v, Instance x) {
		double sum = 0;
		
		for(int i = 0; i != x.size(); i++){
			//sum += v[x.indexes[i]] * x.values[i];
			sum += wGetter(v, x.indexAt(i)) * x.valueAt(i);
		}
		return sum;
	}

	@Override
	public int dim(int src_dim) {		
		return src_dim;
	}

	@Override
	public TreeMap<Long, Double> pipe(Instance x) {
		TreeMap<Long, Double> tm = new TreeMap<Long, Double>();
		for(int i = 0; i != x.size(); i++)
			tm.put((long)x.indexAt(i), x.valueAt(i));
		return tm;
	}

	@Override
	public void add(double[] dense, Instance x, double factor) {
		for(int i = 0; i != x.size(); i++){
			wAdder(dense, x.indexAt(i), x.valueAt(i) * factor);
			//dense[x.indexes[i]] += x.values[i] * factor;
		}
	}

	@Override
	public double snorm(Instance x) {
		double sum = 0; 
		for(int i = 0; i != x.size(); i++)
			sum += x.valueAt(i) * x.valueAt(i);
		
		return sum;
		
	}

	@Override
	public void add(double[] dense, SparseVector x, double factor) {
		for(int i = 0; i != x.numLocations(); i++)
			wAdder(dense, x.indexAtLocation(i), x.valueAtLocation(i) * factor);
	}

	@Override
	public double dot(SparseVector x, SparseVector y) {
		return x.dotProduct(y);
	}

	@Override
	public double dot(double[] dense, SparseVector x) {
		return x.dotProduct(dense);
	}

	@Override
	public double snorm(SparseVector x) {
		double snorm = 0;
		for(int i = 0; i != x.numLocations(); i++)
			snorm += x.valueAtLocation(i) * x.valueAtLocation(i);
		
		return snorm;
	}

	
	
	

}
