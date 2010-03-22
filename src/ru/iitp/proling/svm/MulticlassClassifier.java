package ru.iitp.proling.svm;

public interface MulticlassClassifier<T> {
	double score(SparseVector<?> vec);
	T classify(SparseVector<?> vec);
	double[] scores(SparseVector<?> vec);
}
