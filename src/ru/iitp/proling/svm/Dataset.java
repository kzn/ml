package ru.iitp.proling.svm;

import ru.iitp.proling.common.Alphabet;
import ru.iitp.proling.ml.core.Instance;

/**
 * Dataset - collection of type T
 * @author ant
 *
 * @param <T>
 */
public interface Dataset<T> {
	public Instance<T> get(int idx);
	public int size();
	public int dim();
	public Alphabet<T> alphabet();
}
