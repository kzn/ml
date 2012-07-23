package ru.iitp.proling.feature;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.Arrays;
import java.util.List;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.SparseVector;

public class FeatureVectorBuilder {
	final Alphabet alphabet;
	//final ru.iitp.proling.common.Alphabet<String> s = new ru.iitp.proling.common.Alphabet<String>();
	TIntArrayList indexes = new TIntArrayList();
	TDoubleArrayList values = new TDoubleArrayList();
	
	public FeatureVectorBuilder(Alphabet alphabet){
		this.alphabet = alphabet;
	}
	
	public FeatureVector build(){
		return new FeatureVector(alphabet, indexes.toArray(), values.toArray());
	}
	
	public SparseVector buildSparse(){
		return new SparseVector(indexes.toArray(), values.toArray(), false, true);
	}
	
	/*
	 * Converting values to string is faster than mapping them directly
	 */
	/*public FeatureVectorBuilder add(Object[] key, double value){
		int index = alphabet.lookupIndex(Arrays.asList(key)) + 1;
		indexes.add(index);
		values.add(value);
		return this;
	}*/
	
	/*public FeatureVectorBuilder add(double value, Object key){
		return add(new Object[] {key}, value);
	}*/
	
	public FeatureVectorBuilder add(List<?> key, double value){
		return add(key.toArray(), value);
	}
	
	public FeatureVectorBuilder add(Object... key){
		return add(key, 1.0);
	}
	
	public FeatureVectorBuilder add(Object[] key, double value){
		StringBuilder sb = new StringBuilder();
		for(Object o : key)
			sb.append(o);
		
		int index = alphabet.lookupIndex(sb.toString()) + 1;
		indexes.add(index);
		values.add(value);
		return this;
	}
	

}
