package ru.iitp.proling.ml.core;


/**
 * Generic scoring solver interface 
 * @author ant
 *
 * @param <T>
 */
public interface Solver<T> {
	public T solve(WeightVector wv);
}
