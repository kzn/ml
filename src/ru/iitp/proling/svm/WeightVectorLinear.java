package ru.iitp.proling.svm;

import java.lang.reflect.Array;
import java.util.Arrays;

import ru.iitp.proling.svm.kernel.Kernel;

/**
 * Basic problem formulation for binary classification problem
 * @author ant
 * 
 */

public class WeightVectorLinear extends WeightVector {
	protected Dataset dataset;
	protected double[] alphas;
	protected double[] v;
	protected Kernel kernel;
	protected double[] targets;
	protected double[] snorms;
	
	public WeightVectorLinear(Dataset ds, double[] targets, double[] v, Kernel kernel){
		dataset = ds;
		this.kernel = kernel;
		this.targets = targets;
		this.snorms = new double[ds.size()];
		this.v = v;
		assert(v.length == kernel.dim(dataset.max_dim()) + 1);
		alphas = new double[ds.size()];
	
		// precompute squared norms
		for(int i = 0; i != ds.size(); i++)
			snorms[i] = kernel.snorm(ds.vec(i));
	}
	
	public WeightVectorLinear(Dataset ds, double[] targets, Kernel kernel){
		this(ds, targets, new double[kernel.dim(ds.max_dim()) + 1], kernel);
	}
	
	

	@Override
	public void add(int idx, double factor) {
		kernel.add(v, dataset.vec(idx), factor);
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
		return kernel.dot(v, dataset.vec(idx));
	}
	
	@Override
	public double dot(int x, int y) {
		return kernel.dot(dataset.vec(x), dataset.vec(y));
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
	public double epsilonProjectedGradient(double c) {
		double min_pg = Double.POSITIVE_INFINITY;
		double max_pg = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i != size(); i++){
		    double g = dot(i)*target(i) - 1;
		    double pg = g;
		    double alpha = alpha(i);

		    if(alpha == 0.0)
		      pg = Math.min(g, 0.0);
		    else if(alpha == c)
		      pg = Math.max(g, 0.0);
		    min_pg = Math.min(min_pg, pg);
		    max_pg = Math.max(max_pg, pg);
		  }
		
		return max_pg - min_pg;
	}

	@Override
	public double kktViolation(double c) {
		double viol = 0;

		for(int i = 0; i != size(); i++){
			double alpha = alpha(i);
		    double eval = dot(i)*target(i) - 1;

		    if(alpha == 0.0){
		      if(eval < 0.0)
		    	  viol = Math.max(viol, Math.abs(eval));
		    }else if(alpha == c){
		      if(eval > 0)
		    	  viol = Math.max(viol, Math.abs(eval));
		    }else if(eval >0)
		    	viol = Math.max(viol, Math.abs(eval));
		  }
		  return viol;
	}

	@Override
	public double loss() {
		double sum = 0;
		for(int i = 0; i != size(); i++){
			double res = target(i) * dot(i);
			sum += Math.min(1.0, Math.max(0.0, 1.0 - res));
		  }
		return sum/size();
	}

	@Override
	public double objectiveDual() {
	double res = snorm()/2;
		  
	for(int i = 0; i != size(); i++)
		res -= alpha(i);

	return res;
	}

	@Override
	public double objectivePrimal(double c) {
		return snorm()/2.0 + c * size() * loss();
	}

	@Override
	public double zero_one_loss() {
		int misclassified = 0;
		
		for(int i =0; i != size(); i++)
			if(target(i) * dot(i) < 0)
				misclassified++;
		
		return (double)(misclassified)/size();
	}

	@Override
	public void clear() {
		Arrays.fill(alphas, 0.0);
		Arrays.fill(v, 0.0);
		
	}

}
