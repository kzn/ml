package ru.iitp.proling.svm;
import java.util.List;

public class Sample<T> extends SparseVector<T> {
	public T target;
	
	
	Sample(T target, List<Integer> idxs, List<Double> vals){
		super(idxs, vals, target);
		this.target = target;
	}
}
