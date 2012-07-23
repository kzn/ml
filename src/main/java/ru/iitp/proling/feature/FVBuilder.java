package ru.iitp.proling.feature;


import java.util.List;

import cc.mallet.types.SparseVector;

public interface FVBuilder {
	
	public SparseVector build();
	

	/**
	 * Add binary feature
	 * @param key key of the feature
	 */
	public void add(Object[] key);
	public void add(List<Object> key);
	
	
	/**
	 * Add real numeric feature
	 * @param key key of the feature
	 * @param value value to add
	 */
	public void add(Object[] key, double value);
	public void add(List<Object> key, double value);
	

}
