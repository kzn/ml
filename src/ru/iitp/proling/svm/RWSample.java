package ru.iitp.proling.svm;
import java.util.List;

import ru.iitp.proling.ml.core.Instance;

// ranked and weighted sample	
public class RWSample<T> extends Instance<T> {
	public double weight; // weight
	public int qid; // query id

	RWSample(T target, List<Integer> idxs, List<Double> vals, double weight, int qid){
		super(idxs, vals, target);
		this.qid = qid;
		this.weight = weight;
	}
	
	RWSample(T target, List<Integer> idxs, List<Double> vals, int qid){
		this(target , idxs, vals, 1.0, qid);
	}
}
