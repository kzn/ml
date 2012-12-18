package ru.iitp.proling.svm;

import java.util.ArrayList;
import java.util.List;

import name.kazennikov.ml.core.Instance;

import cc.mallet.types.SparseVector;
import ru.iitp.proling.ml.core.MultiScorer;
import ru.iitp.proling.svm.kernel.Kernel;
import ru.iitp.proling.svm.kernel.LinearKernel;

public class SimpleMultiScorer extends MultiScorer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<double[]> w = new ArrayList<double[]>();
	int n_features = 0;
	Kernel kernel = new LinearKernel();
	
	public SimpleMultiScorer(){

	}
	
	public SimpleMultiScorer(double[][] w, boolean transposed, int nfeatures, int nclasses, Kernel k){
		this.w = new ArrayList<double[]>(nclasses);
		this.n_features = nfeatures;
		
		
		for(int i = 0; i != nclasses; i++){
			this.w.add(new double[n_features]);
			for(int j = 0; j != nfeatures; j++)
					this.w.get(i)[j] = transposed? w[j][i] : w[i][j];
		}
	
		this.kernel = k;
	}

	@Override
	public double score(Instance x, int cls) {
		return kernel.dot(w.get(cls), x);
	}

	@Override
	public double score(SparseVector x, int cls) {
		return kernel.dot(w.get(cls), x);
	}

	@Override
	public int size() {
		return w.size();
	}

	@Override
	public double[] score(SparseVector x) {
		double[] scores = new double[w.size()];
		
		for(int i = 0; i != w.size(); i++)
			scores[i] = score(x, i);

		return scores;
	}
	
	@Override
	public double[] score(Instance x) {
		double[] scores = new double[w.size()];
		
		for(int i = 0; i != w.size(); i++)
			scores[i] = score(x, i);

		return scores;
	}
	
	public void addClass(double[] w){
		this.w.add(w.clone());
	}

}
