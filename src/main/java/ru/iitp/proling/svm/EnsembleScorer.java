package ru.iitp.proling.svm;


import gnu.trove.list.array.TDoubleArrayList;

import java.util.List;

import name.kazennikov.ml.core.Instance;



public class EnsembleScorer extends Scorer {
	
	protected List<Scorer> scorers;
	protected TDoubleArrayList alpha;
	
	public EnsembleScorer(List<Scorer> scorers, TDoubleArrayList alpha){
		assert(scorers.size() == alpha.size());
		this.scorers = scorers;
		this.alpha = alpha;
	}

	@Override
	public double score(Instance v) {
		double res = 0;
		for(int i = 0; i != scorers.size(); i++)
			res += scorers.get(i).score(v)* alpha.get(i);
		return res;
	}
	
	public void addScorer(Scorer scorer, double alpha){
		this.alpha.add(alpha);
		this.scorers.add(scorer);
	}
	
	public int size(){
		return alpha.size();
	}

}
