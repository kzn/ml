package ru.iitp.proling.svm.kernel;

import java.util.TreeMap;

import ru.iitp.proling.svm.SparseVector;

public class LinearKernel extends Kernel {

	@Override
	public double dot(SparseVector x, SparseVector y) {
		double res = 0;
		int j = 0;
		int k = 0;
		
		while(j != x.indexes().length	 && k != y.indexes().length){
			if(x.indexes()[j] < y.indexes()[k])
				j++;
			else if(x.indexes()[j] > y.indexes()[k])
				k++;
			else{
				res += x.values()[j] * y.values()[k];
				j++;
				k++;
			}
		}
		
		return res;

	}

	@Override
	public double dot(double[] v, SparseVector x) {
		double sum = 0;
		
		for(int i = 0; i != x.indexes().length; i++){
			//sum += v[x.indexes[i]] * x.values[i];
			sum += wGetter(v, x.indexes()[i]) * x.values()[i];
		}
		return sum;
	}

	@Override
	public int dim(int src_dim) {		
		return src_dim;
	}

	@Override
	public TreeMap<Long, Double> pipe(SparseVector x) {
		TreeMap<Long, Double> tm = new TreeMap<Long, Double>();
		for(int i = 0; i != x.size(); i++)
			tm.put((long)x.indexes()[i], x.values()[i]);
		return tm;
	}

	@Override
	public void add(double[] dense, SparseVector x, double factor) {
		for(int i = 0; i != x.size(); i++){
			wAdder(dense, x.indexes()[i], x.values()[i] * factor);
			//dense[x.indexes[i]] += x.values[i] * factor;
		}
	}

	@Override
	public double snorm(SparseVector x) {
		double sum = 0; 
		for(int i = 0; i != x.size(); i++)
			sum += x.values()[i] * x.values()[i];
		
		return sum;
		
	}

	
	
	

}
