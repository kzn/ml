package ru.iitp.proling.ml.scorer;

import cc.mallet.types.SparseVector;
import ru.iitp.proling.ml.core.Instance;

public abstract class ScoreProcessor extends Scorer {
	final Scorer base;
	
	public ScoreProcessor(Scorer base){
		this.base = base;
		
	}
	
	public abstract double transform(double score);

	@Override
	public double score(Instance v) {
		return transform(base.score(v));
	}

	@Override
	public double score(SparseVector v) {
		return transform(base.score(v));
	}

}
