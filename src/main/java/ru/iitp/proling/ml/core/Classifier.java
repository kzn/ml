package ru.iitp.proling.ml.core;

import java.io.Serializable;

import cc.mallet.types.SparseVector;

/**
 * Generic user classifier 
 * @author ant
 *
 * @param <T>
 */
public interface Classifier<T> extends Serializable{
	public T classify(Instance vec);
	public T classify(SparseVector vec);
	public double score(Instance vec, int cls);
	public double score(SparseVector vec, int cls);
	
	int classes();
	
}
