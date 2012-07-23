package ru.iitp.proling.svm;

import java.util.Arrays;
import java.util.List;

import cc.mallet.types.InstanceList;
import cc.mallet.types.SparseVector;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.svm.kernel.Kernel;

public class WeightVectorMallet extends WeightVector {
	protected List<Instance> dataset;
	protected double[] alphas;
	protected double[] v;
	protected Kernel kernel;
	protected double[] targets;
	protected double[] snorms;
	
	static int computeDim(List<Instance> vecs){
		int dim = 0;
		for(Instance v : vecs)
			dim = Math.max(dim, v.dim());
		
		return dim;
	}
	
	public WeightVectorMallet(List<Instance> ds, double[] targets, double[] v, Kernel kernel){
		dataset = ds;
		this.kernel = kernel;
		this.targets = targets;
		this.snorms = new double[ds.size()];
		this.v = v;
		assert(v.length == kernel.dim(computeDim(ds)) + 1);
		alphas = new double[ds.size()];
	
		// precompute squared norms
		for(int i = 0; i != ds.size(); i++)
			snorms[i] = kernel.snorm(ds.get(i));
	}
	
	public WeightVectorMallet(List<Instance> ds, double[] targets, Kernel kernel){
		this(ds, targets, new double[kernel.dim(computeDim(ds)) + 1], kernel);
	}
	

	@Override
	public void add(int idx, double factor) {
		kernel.add(v, dataset.get(idx), factor);
	}

	@Override
	public void add_alpha(int idx, double factor) {
		alphas[idx] += factor;
	}

	@Override
	public double alpha(int idx) {
		return alphas[idx];
	}

	@Override
	public int dim() {
		return v.length;
	}

	@Override
	public double dot(int idx) {
		return kernel.dot(v, dataset.get(idx));
	}
	
	@Override
	public double dot(int x, int y) {
		return kernel.dot(dataset.get(x), dataset.get(y));
	}
	

	@Override
	public int size() {
		return dataset.size();
	}

	@Override
	public double norm() {
		return Math.sqrt(snorm());
	}
	
	@Override
	public double snorm(){
		double n = 0;
		
		for(int i = 0; i != dim(); i++)
			n += v[i] * v[i];
		
		return n;
	
	}

	@Override
	public double snorm(int idx) {
		return snorms[idx];
	}

	@Override
	public double target(int idx) {
		return targets[idx];
	}
	
	@Override
	public double[] vec(){
		return v;
	}
	
	@Override
	public void scale(double k){
		for(int i = 0; i != v.length; i++)
			v[i] *= k;
	}

	@Override
	public void clear() {
		Arrays.fill(alphas, 0.0);
		Arrays.fill(v, 0.0);
		
	}
	
	@Override
	public Kernel kernel(){
		return kernel;
	}


}
