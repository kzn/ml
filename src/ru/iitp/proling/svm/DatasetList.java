package ru.iitp.proling.svm;

import java.util.List;


public interface DatasetList<T>{
	
	public int size();
	public List<Instance<T>> get(int idx);
	public Dataset<T> instances();
	public int[] queryID();
}
