package ru.iitp.proling.svm;

import ru.iitp.proling.ml.core.MultiScorer;
import ru.iitp.proling.ml.core.MulticlassProblem;
import ru.iitp.proling.ml.core.MulticlassSolver;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.svm.kernel.Kernel;

public class MulticlassOVRSolver implements MulticlassSolver {
	double c = 1;
	BinarySolver solver;
	Kernel kernel;
	
	public MulticlassOVRSolver(double c, BinarySolver solver, Kernel kernel){
		this.c = c;
		this.solver = solver;
		this.kernel = kernel;
	}

	@Override
	public MultiScorer solve(MulticlassProblem problem) {
		SimpleMultiScorer scorer = new SimpleMultiScorer();
		
		for(int i = 0; i != problem.classes(); i++){
			// constructing i-th OVR classifier
			double[] targets = new double[problem.size()];
			for(int j = 0; j != problem.size(); j++)
				targets[j] = problem.target(j) == i? 1 : -1;
			WeightVector wv = new WeightVectorMallet(problem.instances(), targets, kernel);
			solver.solve(wv);
			scorer.addClass(wv.vec());
			
		}
		
		return scorer;
	}

}
