package ru.iitp.proling.svm;

import ru.iitp.proling.ml.core.Instance;

/**
 * Binary classification common class. Basic interface to binary predictor that has 2 outcomes.
 * This a indirect interface, as any binary classifier expected to conform to defined functions
 * @author ant
 *
 */
public class BinaryClassifier<T>{
	protected Scorer scorer;
	T positive;
	T negative;
	
	public BinaryClassifier(Scorer scorer, T positive, T negative){
		this.scorer = scorer;
		this.positive = positive;
		this.negative = negative;
	}


	/**
	 * Classify vector
	 * @param v vector
	 * @return class of vector, -1 if negative in case of indicator classification
	 */
	T classify(Instance<?> v) {
		return score(v) > 0.0? positive : negative;
	}
	
	/**
	 * Get score of vector v, by this classifier. Can be used for various generalization performance measures.
	 * @param v vector
	 * @return score 
	 */
	double score(Instance<?> v){
		return scorer.score(v);
	}


}
