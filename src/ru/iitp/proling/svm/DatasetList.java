package ru.iitp.proling.svm;

import java.util.List;
import java.util.SortedSet;

public interface DatasetList<T>{
	
	public List<SparseVector<T>> get(int idx);
	public int size();
	public int dim();
	
	public SortedSet<T>	classes();
}
