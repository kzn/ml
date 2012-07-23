package ru.iitp.proling.ml.core;

import java.io.Serializable;

import ru.iitp.proling.ml.scorer.Scorer;

import cc.mallet.types.SparseVector;

/**
 * Base class for multiple score objects. Used when the decision in made using several scores
 * instead of single score as in {@link Scorer}. For example in multiclass classification.
 * 
 *  However, its size may not equal to number of actual classes. Consider the number of weight
 *  vectors in OvO multiclass problem solution and actual number of score that neede to make a decision.
 *  So, it is possible to retrieve all scores in one operation. It is guaranteed that the size of returned
 *  array is size() 
 * @author ant
 *
 */
public abstract class MultiScorer implements Serializable{
	public abstract int size();
	
	public abstract double score(Instance x, int index);
	public abstract double score(SparseVector x, int index);
	public abstract double[] score(SparseVector x);
	public abstract double[] score(Instance x);
}
