package ru.iitp.proling.ml.sgd;

public interface LossFunction {
	public double loss(double a, double y);
	public double dloss(double a, double y);
	
}
