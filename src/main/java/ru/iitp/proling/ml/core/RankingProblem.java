package ru.iitp.proling.ml.core;

import java.util.List;

import name.kazennikov.ml.core.Instance;

/**
 * Rank problem formulation. Target represent instance places in ranking.
 * That means than '1' is the first element, '2' - means that is ranked after '1' etc.
 * The order of elements with same rank is undefined. 
 * @author ant
 *
 */
public abstract class RankingProblem {
	
	public abstract int size();
	public abstract int dim();
	public abstract List<Instance> get(int index);
	public abstract int[] target(int index);
	
	public double cost(int index){
		return 1.0;
	}
	
	// pairwise problem formulation
	public abstract int pairSize();
	public abstract Instance pairGreater(int index);
	public abstract Instance pairLesser(int index);
	public abstract int rankDifference(int index); // rank difference between pairGreater(i) and pairLesser(i)
	

}
