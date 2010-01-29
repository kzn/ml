package ru.iitp.proling.svm;

import java.util.Arrays;

public class WeightVectorCost extends WeightVectorProxy {
	
	protected double[] costs;
	
	public WeightVectorCost(WeightVector base, double[] costs){
		super(base);
		this.costs = costs;
		if(this.costs == null){
			this.costs = new double[base.size()];
			Arrays.fill(this.costs, 1.0);
		}
	}
	
	public double cost(int idx){
		return costs[idx];
	}
	
	public void set_cost(int idx, double val){
		costs[idx] = val;
	}

}
