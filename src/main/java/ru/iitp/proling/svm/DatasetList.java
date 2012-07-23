package ru.iitp.proling.svm;

import java.util.List;

import ru.iitp.proling.ml.core.Dataset;
import ru.iitp.proling.ml.core.NativeInstance;


public interface DatasetList<T>{
	
	public int size();
	public List<NativeInstance<T>> get(int idx);
	public Dataset<T> instances();
	public int[] queryID();
}
