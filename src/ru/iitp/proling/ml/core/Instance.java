package ru.iitp.proling.ml.core;
import java.util.List;

/**
 * Generic instance class. Represent an ML learning/testing instance.
 * @author ant
 *
 * @param <T>
 */
public class Instance<T> {
	final int indexes[];
	final double values[];
	double weight = 1.0;
	T value;
	
	public Instance(List<Integer> idxs, List<Double> vals){
		assert(idxs.size() == vals.size());
		indexes = new int[idxs.size()];
		values = new double[idxs.size()];
		
		for(int i = 0; i != idxs.size(); i++){
			indexes[i] = idxs.get(i);
			values[i] = vals.get(i);
		}
	}
	
	public Instance(List<Integer> idxs, List<Double> vals, T value){
		this(idxs, vals);
		this.value = value;
	}
	
	public Instance(List<Integer> idxs, List<Double> vals, T value, double weight){
		this(idxs, vals, value);
		this.weight = weight;
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
	
	public double weight(){
		return weight;
	}
	}
};
