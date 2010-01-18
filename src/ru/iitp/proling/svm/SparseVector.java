package ru.iitp.proling.svm;
import java.util.List;

public class SparseVector {
	public int indexes[];
	public double values[];
	
	SparseVector(List<Integer> idxs, List<Double> vals){
		assert(idxs.size() == vals.size());
		indexes = new int[idxs.size()];
		values = new double[idxs.size()];
		
		for(int i = 0; i != idxs.size(); i++){
			indexes[i] = idxs.get(i);
			values[i] = vals.get(i);
		}
	}
};
