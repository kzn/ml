package ru.iitp.proling.svm;

import ru.iitp.proling.svm.kernel.Kernel;

public abstract class Problem {
	public abstract Classifier solve(Dataset dataset, Kernel kernel, Solver solver);
}
