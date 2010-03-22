package ru.iitp.proling.ml.core;

import java.util.SortedSet;

public interface Dataset<T> {
	public SortedSet<T> classes();
	public Instance<T> get(int idx);
	public int size();
	public int dim();
	public String name();
}
