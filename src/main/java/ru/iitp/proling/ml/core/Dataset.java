package ru.iitp.proling.ml.core;

import ru.iitp.proling.common.Alphabet;

/**
 * Dataset - collection of type T
 * @author ant
 *
 * @param <T>
 */
public interface Dataset<T> {
	public NativeInstance<T> get(int idx);
	public int size();
	public int dim();
	public Alphabet<T> alphabet();
}
