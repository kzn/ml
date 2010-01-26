package ru.iitp.proling.svm;

public class WeightVectorProxy extends WeightVector {
	protected WeightVector w;
	
	public WeightVectorProxy(WeightVector base){
		w = base;
	}

	@Override
	public void add(int idx, double factor) {
		w.add(idx, factor);
	}

	@Override
	public void add_alpha(int idx, double factor) {
		w.add_alpha(idx, factor);
	}

	@Override
	public double alpha(int idx) {
		return w.alpha(idx);
	}

	@Override
	public int dim() {
		return w.dim();
	}

	@Override
	public double dot(int idx) {
		return w.dot(idx);
	}

	@Override
	public double dot(int x, int y) {
		return w.dot(x,y);
	}

	@Override
	public double norm() {
		return w.norm();
	}

	@Override
	public int size() {
		return w.size();
	}

	@Override
	public double snorm() {
		return w.snorm();
	}

	@Override
	public double snorm(int idx) {
		return w.snorm(idx);
	}

	@Override
	public double target(int idx) {
		return w.target(idx);
	}
	
	@Override
	public double[] vec(){
		return w.vec();
	}

	@Override
	public void scale(double k) {
		w.scale(k);
	}

	@Override
	public double epsilonProjectedGradient(double c) {
		return w.epsilonProjectedGradient(c);
	}

	@Override
	public double kktViolation(double c) {
		return w.kktViolation(c);
	}

	@Override
	public double loss() {
		return w.loss();
	}

	@Override
	public double objectiveDual() {
		return w.objectiveDual();
	}

	@Override
	public double objectivePrimal(double lambda) {
		return w.objectivePrimal(lambda);
	}

	@Override
	public double zero_one_loss() {
		return w.zero_one_loss();	
	}

	@Override
	public void clear() {
		w.clear();
		
	}

}
