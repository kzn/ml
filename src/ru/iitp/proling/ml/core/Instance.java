package ru.iitp.proling.ml.core;

public interface Instance<T> extends Iterable<Feature>{
	public T value();
	public double get(int idx);
	public int[] indexes();
	public double[] values();
}
