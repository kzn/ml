package ru.iitp.proling.svm;

public class BinarySolverBuilderPegasos extends BinarySolverBuilder {
	
	public int iter;
	public int k;
	public double c;
	public int start_iter;
	
	
	public BinarySolverBuilderPegasos(int iter, int k, double c, int start_iter) {
		
		this.iter = iter;
		this.k = k;
		this.c = c;
		this.start_iter = start_iter;
	}

	@Override
	public BinarySolver create() {
		return new PegasosSolver(iter, k, c, start_iter);
	}

}
