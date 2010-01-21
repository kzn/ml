package ru.iitp.proling.svm;

public interface MulticlassClassifier {
	double score(SparseVector vec);
	int classify(SparseVector vec);
	double[] scores(SparseVector vec);
}
