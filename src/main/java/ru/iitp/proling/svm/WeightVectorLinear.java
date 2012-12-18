package ru.iitp.proling.svm;

import java.util.Arrays;
import java.util.List;

import name.kazennikov.ml.core.Instance;

import ru.iitp.proling.ml.core.Dataset;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.svm.kernel.Kernel;

/**
 * Basic problem formulation for binary classification problem
 * @author ant
 * 
 */
public class WeightVectorLinear extends WeightVector {
	protected List<? extends Instance> dataset;
	//protected Dataset<?> dataset;
	protected double[] alphas;
	protected double[] v;
	protected Kernel kernel;
	protected double[] targets;
	protected double[] snorms;
	
	public WeightVectorLinear(List<? extends Instance> dataset, double[] targets, double[] v, Kernel kernel){
		this.dataset = dataset;
		this.kernel = kernel;
		this.targets = targets;
		this.snorms = new double[dataset.size()];
		this.v = v;
		assert(v.length == kernel.dim(dim(dataset)) + 1);
		alphas = new double[dataset.size()];
		System.out.printf("WV dim: %d%n",v.length);
		// precompute squared norms
		for(int i = 0; i != dataset.size(); i++)
			snorms[i] = kernel.snorm(dataset.get(i));
	}
	
	public static int dim(List<? extends Instance> instances) {
		int dim = 0;
		for(Instance i : instances) {
			if(i.dim() > dim) {
				dim = i.dim();
			}
		}
		
		return dim;
	}
	
	public WeightVectorLinear(List<? extends Instance> dataset, double[] targets, Kernel kernel){
		this(dataset, targets, new double[kernel.dim(dim(dataset)) + 1], kernel);
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
