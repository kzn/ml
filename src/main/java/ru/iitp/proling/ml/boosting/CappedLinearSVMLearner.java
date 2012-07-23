package ru.iitp.proling.ml.boosting;

import java.util.List;

import cc.mallet.types.SparseVector;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.scorer.ScoreProcessor;
import ru.iitp.proling.ml.scorer.Scorer;

public class CappedLinearSVMLearner extends LinearSVMWeakLearner {

	public CappedLinearSVMLearner(double cPos, double cNeg, int svmIters) {
		super(cPos, cNeg, svmIters);
	}
	
	@Override
	public Scorer train(List<Instance> instances, double[] targets, double[] weights){
		final Scorer base = super.train(instances, targets, weights);
		
		
		return new ScoreProcessor(base) {
			
			@Override
			public double transform(double score) {
				double res = score;
				if(score > 1.0)
					res = 1.0;
				if(score < -1.0)
					res = -1.0;
				return res;
			}
		};
	}

}
