package ru.iitp.proling.svm;

import name.kazennikov.ml.core.Instance;

public abstract class Scorer {
	public abstract double score(Instance v);
}
