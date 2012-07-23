package ru.iitp.proling.ml.scorer;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.List;

import cc.mallet.types.SparseVector;

import ru.iitp.proling.ml.core.Instance;


public class EnsembleScorer extends Scorer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected List<Scorer> scorers;
	protected TDoubleArrayList alpha;
	
	public EnsembleScorer(List<Scorer> scorers, TDoubleArrayList alpha){
		assert(scorers.size() == alpha.size());
		this.scorers = scorers;
		this.alpha = alpha;
	}
	
	public EnsembleScorer(EnsembleScorer base, int n){
		this.scorers = new ArrayList<Scorer>(n);
		this.alpha = new TDoubleArrayList(n);
		
		if(n > base.scorers.size())
			n = base.scorers.size();
		
		for(int i = 0; i != n; i++){
			this.scorers.add(base.scorers.get(i));
			this.alpha.add(base.alpha.get(i));
		}
	}

	@Override
	public double score(Instance v) {
		double res = 0;
		for(int i = 0; i != scorers.size(); i++)
			res += scorers.get(i).score(v) * alpha.get(i);
		return res;
	}
	
	public void addScorer(Scorer scorer, double alpha){
		this.alpha.add(alpha);
		this.scorers.add(scorer);
	}
	
	public int size(){
		return alpha.size();
	}


	@Override
	public double score(SparseVector v) {
		double res = 0;
		for(int i = 0; i != scorers.size(); i++)
			res += scorers.get(i).score(v) * alpha.get(i);
		return res;
	}

}
