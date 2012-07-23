package ru.iitp.proling.ml.core;


import gnu.trove.map.hash.TIntDoubleHashMap;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Generic instance class. Represent an ML learning/testing instance.
 * @author ant
 *
 * @param <T>
 */

public class NativeInstance<T> implements Instance, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int indexes[];
	final double values[];
	double weight = 1.0;
	T value;

	public NativeInstance(List<Integer> idxs, List<Double> vals){
		assert(idxs.size() == vals.size());
		indexes = new int[idxs.size()];
		values = new double[idxs.size()];

		for(int i = 0; i != idxs.size(); i++){
			indexes[i] = idxs.get(i);
			values[i] = vals.get(i);
		}
	}
	
	public NativeInstance(TIntDoubleHashMap fv, T value){
		TreeMap<Integer, Double> tm = new TreeMap<Integer, Double>();
		indexes = new int[fv.size()];
		values = new double[fv.size()];
		
		int[] keys = fv.keys();
		double[] vals = fv.values();
		this.value = value;

		
		for(int i = 0; i != fv.size(); i++){
			tm.put(keys[i] + 1, vals[i]);
		}
		int i = 0;
		for(Entry<Integer, Double> entry : tm.entrySet()){
			indexes[i] = entry.getKey();
			values[i] = entry.getValue();
			i++;
		}
	}

	public NativeInstance(List<Integer> idxs, List<Double> vals, T value){
		this(idxs, vals);
		this.value = value;
	}

	public NativeInstance(List<Integer> idxs, List<Double> vals, T value, double weight){
		this(idxs, vals, value);
		this.weight = weight;
	}
	
	public NativeInstance(int[] indexes, double[] vals, T value){
		this.indexes = indexes;
		this.values = vals;
		this.value = value;
	}
	
	public NativeInstance(int[] indexes, double[] vals, T value, double weight){
		this(indexes, vals, value);
		this.weight = weight;
	}


	public int dim(){
		return indexes.length > 0? indexes[indexes.length - 1] : 0;
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

	@Override
	public int indexAt(int index) {
		return indexes[index];
	}

	@Override
	public int size() {
		return indexes.length;
	}

	@Override
	public double valueAt(int index) {
		return values[index];
	}


}
