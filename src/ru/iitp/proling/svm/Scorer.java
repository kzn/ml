package ru.iitp.proling.svm;

public abstract class Scorer {
	public abstract double score(SparseVector v);
}
