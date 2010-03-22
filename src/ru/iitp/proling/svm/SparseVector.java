package ru.iitp.proling.svm;
import java.util.List;

public class SparseVector<T> {
	final int indexes[];
	final double values[];
	T value;
	
	SparseVector(List<Integer> idxs, List<Double> vals, T value){
		assert(idxs.size() == vals.size());
		indexes = new int[idxs.size()];
		values = new double[idxs.size()];
		
		for(int i = 0; i != idxs.size(); i++){
			indexes[i] = idxs.get(i);
			values[i] = vals.get(i);
		}
		
		this.value = value;
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
	
	public int[] indexes(){
		return indexes;
	}
	
	public double[] values(){
		return values;
	}
	
	public T value(){
		return value;
	}
};
