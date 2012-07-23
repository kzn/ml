package ru.iitp.proling.ml.boosting;

import java.util.List;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.scorer.Scorer;

/**
 * Weak learner interface for boosting algorithms.
 * @author ant
 *
 */
public interface WeakLearner {
	/**
	 * Train a weak learner. 
	 * @param instances list of training instances
	 * @param targets targets of training instances
	 * @param weights normalized weights of each instance, sum(weights) = 1.0
	 * @return scorer, for which sign(score(x)) represent its class
	 */
	public Scorer train(List<Instance> instances, double[] targets, double[] weights);

}
