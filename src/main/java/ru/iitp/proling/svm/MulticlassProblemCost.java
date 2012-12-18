package ru.iitp.proling.svm;

import name.kazennikov.ml.core.MulticlassProblem;

public class MulticlassProblemCost extends MulticlassProblemProxy {
	final double[] costs;

	MulticlassProblemCost(MulticlassProblem problem, double[] cost) {
		super(problem);
		assert problem.size() == cost.length;
		costs = cost;
	}
	
	@Override
	public double cost(int index){
		return costs[index];
	}
	
	public void setCost(int index, double newCost){
		costs[index] = newCost;
	}

}
