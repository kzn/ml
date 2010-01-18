package ru.iitp.proling.svm;
import java.util.List;

public class Sample extends SparseVector {
	public double target;
	public int size;
	
	
	Sample(double target, List<Integer> idxs, List<Double> vals){
		super(idxs, vals);
		this.target = target;
		this.size = idxs.size();
	}
}
