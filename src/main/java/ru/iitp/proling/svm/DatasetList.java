package ru.iitp.proling.svm;

import java.util.List;

import name.kazennikov.ml.core.NativeInstance;

import ru.iitp.proling.ml.core.Dataset;


public interface DatasetList<T>{
	
	public int size();
	public List<NativeInstance<T>> get(int idx);
	public Dataset<T> instances();
	public int[] queryID();
}
