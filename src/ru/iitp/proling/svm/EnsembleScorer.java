package ru.iitp.proling.svm;

import java.util.List;

import ru.iitp.proling.svm.kernel.Kernel;

public class EnsembleScorer extends Scorer {
	
	protected List<double[]> models;
	protected double[] alpha;
	protected Kernel kernel;
	
	public EnsembleScorer(List<double[]> models, double[] alpha, Kernel kernel){
		assert(models.size() == alpha.length);
		this.models = models;
		this.alpha = alpha;
	}

	@Override
	public double score(SparseVector v) {
		double res = 0;
		for(int i = 0; i != models.size(); i++)
			res += kernel.dot(models.get(i), v) * alpha[i];
		
		return res;
	}

}
