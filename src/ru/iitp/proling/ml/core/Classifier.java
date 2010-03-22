package ru.iitp.proling.ml.core;

import gnu.trove.TObjectDoubleHashMap;
import ru.iitp.proling.svm.SparseVector;

public interface Classifier<T> {
	public T classify(SparseVector vec);
	public TObjectDoubleHashMap<T> scores(SparseVector vec);
	

}
