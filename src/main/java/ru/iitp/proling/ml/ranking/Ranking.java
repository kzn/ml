package ru.iitp.proling.ml.ranking;

import cc.mallet.types.Instance;

/**
 * Generic result of ranking a query.
 * @author ant
 *
 */
public abstract class Ranking {
	Instance instance;
	Ranker ranker;
	int[] ranking;
	
	
	public Ranking(Instance instance, Ranker ranker, int[] ranking){
		this.instance = instance;
		this.ranker = ranker;
		this.ranking = ranking;
	}
	
	/**
	 * Check if best item is in top-n if ranking
	 * @param n
	 * @return
	 */
	public boolean bestInTop(int n){
		
		
		return false;
	}
	

}
