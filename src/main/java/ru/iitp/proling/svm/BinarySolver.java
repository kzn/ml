package ru.iitp.proling.svm;

import ru.iitp.proling.ml.core.Solver;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.Scorer;


/**
 * Binary classification problem solver. Returns a scorer object which produce,
 * which is(can) interpreted as f(x) > 0 => 1, f(x) < 0 => -1.
 * Optionally it returns score of the x. Score is a double value;
 * @author ant
 *
 */
public abstract class BinarySolver implements Solver<Scorer>{
	public abstract Scorer solve(WeightVector wv);
	
}
