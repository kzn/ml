package ru.iitp.proling.ml.core;

import gnu.trove.TObjectDoubleHashMap;
import ru.iitp.proling.svm.LabeledDataset;
import ru.iitp.proling.svm.Instance;

public interface Classifier<T> {
	public T classify(Instance<?> vec);
	public TObjectDoubleHashMap<T> scores(Instance<?> vec);
	public void build(LabeledDataset<T> dataset);
}
