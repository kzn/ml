package ru.iitp.proling.ml.scorer;

import cc.mallet.types.SparseVector;
import ru.iitp.proling.ml.core.Instance;

public class DiscreteScorer extends Scorer {
	final Scorer base;
	final double threshold;
	final double greater;
	final double lesser;
	
	public DiscreteScorer(Scorer base, double threshold, double greater, double lesser){
		this.base = base;
		this.threshold = threshold;
		this.greater = greater;
		this.lesser = lesser;
	}

	@Override
	public double score(Instance v) {
		return base.score(v) > threshold? greater : lesser;
	}

	@Override
	public double score(SparseVector v) {
		return base.score(v) > threshold? greater : lesser;
	}

}
