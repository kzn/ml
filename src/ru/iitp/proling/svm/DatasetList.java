package ru.iitp.proling.svm;

import java.util.List;

import ru.iitp.proling.ml.core.Dataset;
import ru.iitp.proling.ml.core.Instance;


public interface DatasetList<T>{
	
	public int size();
	public List<Instance<T>> get(int idx);
	public Dataset<T> instances();
	public int[] queryID();
}
