package ru.iitp.proling.svm;

import ru.iitp.proling.ml.core.Instance;

/**
 * Generic multiclass classifier interface
 * @author ant
 *
 * @param <T>
 */
public interface MulticlassClassifier<T> {
	double score(Instance vec);
	T classify(Instance vec);
	double[] scores(Instance vec);
}