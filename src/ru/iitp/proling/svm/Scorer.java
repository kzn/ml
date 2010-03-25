package ru.iitp.proling.svm;

import ru.iitp.proling.ml.core.Instance;

public abstract class Scorer {
	public abstract double score(Instance v);
}
