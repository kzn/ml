package ru.iitp.proling.svm;

public class WeightVectorOVR extends WeightVectorProxy {
	protected double positive;
	
	public double getPositive() {
		return positive;
	}
	public void setPositive(double positive) {
		this.positive = positive;
	}
	public WeightVectorOVR(WeightVector base, double positive){
		super(base);
		this.positive = positive;
	}

}
