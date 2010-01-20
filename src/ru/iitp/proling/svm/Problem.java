package ru.iitp.proling.svm;

public abstract class Problem {
	public abstract Classifier solve(Dataset dataset, Solver solver);
}
