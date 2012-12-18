package ru.iitp.proling.ml.scorer;

import name.kazennikov.ml.core.Instance;
import cc.mallet.types.SparseVector;

public class BiasedScorer extends Scorer {
	
	Scorer base;
	public double bias = 0;
	
	public BiasedScorer(Scorer base) {
		this.base = base;
	}
	

	@Override
	public double score(Instance v) {
		return base.score(v) + bias;
	}

	@Override
	public double score(SparseVector v) {
		return base.score(v) + bias;
	}

}
