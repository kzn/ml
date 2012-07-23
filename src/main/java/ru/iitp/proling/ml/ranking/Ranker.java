package ru.iitp.proling.ml.ranking;

import cc.mallet.types.Instance;

public abstract class Ranker {
	/**
	 * Score one instance, whose data is a sparse vector
	 * @param instance instance to score
	 * @return relevance score
	 */
	public abstract double score(Instance instance);
	
	/**
	 * Rank one instance of list of feature vectors
	 * @param instance instance with list feature vector as data
	 * @return a Ranking
	 */
	public abstract Ranking rank(Instance instance); 
	

}
