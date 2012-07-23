package ru.iitp.proling.ml.core;


public interface MulticlassSolver {
	public MultiScorer solve(MulticlassProblem problem);
}
