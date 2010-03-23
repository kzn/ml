package ru.iitp.proling.svm;

public interface MulticlassClassifier<T> {
	double score(Instance<?> vec);
	T classify(Instance<?> vec);
	double[] scores(Instance<?> vec);
}
