package ru.iitp.proling.svm;
import java.util.List;

// ranked and weighted sample	
public class RWSample extends Sample {
	public double weight; // weight
	public int qid; // query id

	RWSample(double target, List<Integer> idxs, List<Double> vals, double weight, int qid){
		super(target, idxs, vals);
		this.qid = qid;
		this.weight = weight;
	}
	
	RWSample(double target, List<Integer> idxs, List<Double> vals, int qid){
		this(target, idxs, vals, 1.0, qid);
	}
}
