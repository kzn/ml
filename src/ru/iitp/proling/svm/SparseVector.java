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
	
	public int dim(){
		return indexes[indexes.length - 1];
	}
	
	public int size(){
		return indexes.length;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i != size(); i++){
			if(i != 0)
				sb.append(' ');
			sb.append(indexes[i]);
			sb.append(':');
			sb.append(values[i]);
		}
		
		return sb.toString();
	}
};
