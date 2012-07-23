package ru.iitp.proling.ml.boosting;

import java.util.List;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.WeightVectorCost;
import ru.iitp.proling.svm.WeightVectorMallet;
import ru.iitp.proling.svm.kernel.LinearKernel;

public class LinearSVMWeakLearner implements WeakLearner{
	double cPos;
	double cNeg;
	int svmIters;
	
	public LinearSVMWeakLearner(double cPos, double cNeg, int svmIters){
		this.cPos = cPos;
		this.cNeg = cNeg;
		this.svmIters = svmIters;
		
	}

	@Override
	public Scorer train(List<Instance> instances, double[] targets, double[] weights) {
		WeightVectorMallet wv = new WeightVectorMallet(instances, targets, new LinearKernel());
		WeightVectorCost wvc = new WeightVectorCost(wv, weights);

		return DCDSolver.solve(wvc, cPos, cNeg, svmIters, 0.1, 200000, 1);
	}

}
