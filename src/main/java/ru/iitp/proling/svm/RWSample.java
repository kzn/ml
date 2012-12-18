package ru.iitp.proling.svm;

import gnu.trove.map.hash.TIntDoubleHashMap;

import java.io.Serializable;
import java.util.List;

import name.kazennikov.ml.core.NativeInstance;
import name.kazennikov.ml.core.SimpleInstance;


// ranked and weighted sample	
public class RWSample<T> extends NativeInstance<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int qid; // query id
	

	public RWSample(T target, List<Integer> idxs, List<Double> vals, double weight, int qid){
		super(idxs, vals, target, weight);
		this.qid = qid;
	}
	
	public RWSample(T target, int[] indexes, double[] values, double weight, int qid){
		super(indexes, values, target, weight);
		this.qid = qid;
	}
	
	public RWSample(T target, TIntDoubleHashMap fv, int qid){
		super(fv, target);
		this.qid = qid;
	}
	
	
	public RWSample(T target, List<Integer> idxs, List<Double> vals, int qid){
		this(target , idxs, vals, 1.0, qid);
	}	
}
