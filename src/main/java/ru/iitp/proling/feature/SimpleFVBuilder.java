package ru.iitp.proling.feature;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import ru.iitp.proling.common.StringMapper;
import cc.mallet.types.SparseVector;

public class SimpleFVBuilder implements FVBuilder {
	StringMapper sm;
	TIntArrayList indexes = new TIntArrayList();
	TDoubleArrayList values = new TDoubleArrayList();
	
	
	public SimpleFVBuilder(StringMapper sm){
		this.sm = sm;
	}

	@Override
	public void add(Object[] key) {
		add(key, 1.0);
	}

	@Override
	public void add(Object[] key, double value) {
		indexes.add(sm.lookup(key(key)));
		values.add(value);
	}

	@Override
	public SparseVector build() {

		return new SparseVector(indexes.toArray(), values.toArray(), false, true, true);
	}
	
	String key(Object[] key){
		StringBuilder sb = new StringBuilder();
		for(Object k : key){
			sb.append(k.toString());
		}
		return sb.toString();
	}
	
	String key(List<Object> key){
		StringBuilder sb = new StringBuilder();
		for(Object k : key){
			sb.append(k.toString());
		}
		return sb.toString();
	}

	@Override
	public void add(List<Object> key) {
		add(key, 1.0);
	}

	@Override
	public void add(List<Object> key, double value) {
		indexes.add(sm.lookup(key(key)));
		values.add(value);
	}
}
