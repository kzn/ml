package ru.iitp.proling.ml.core;

import gnu.trove.TObjectDoubleHashMap;
import ru.iitp.proling.svm.Dataset;
/**
 * End user classifier 
 * @author ant
 *
 * @param <T>
 */
public interface Classifier<T> {
	public T classify(Instance<?> vec);
	public TObjectDoubleHashMap<T> scores(Instance<?> vec);
	public void build(Dataset<T> dataset);
}
