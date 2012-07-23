package ru.iitp.proling.ml.mallet;

import java.util.ArrayList;
import java.util.List;

import ru.iitp.proling.ml.core.MultiScorer;
import ru.iitp.proling.ml.core.MulticlassProblem;
import ru.iitp.proling.ml.core.MulticlassSolver;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.MulticlassCSSolver;
import ru.iitp.proling.svm.MulticlassProblemBasic;
import ru.iitp.proling.svm.WeightVectorMallet;
import ru.iitp.proling.svm.kernel.LinearKernel;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.SparseVector;

public class SVMMultiClassifierTrainer extends ClassifierTrainer<SVMMultiClassifier> {
	
	SVMMultiClassifier classifier;
	MulticlassSolver solver;
	
	public SVMMultiClassifierTrainer(MulticlassSolver solver){
		this.solver = solver;
	}

	@Override
	public SVMMultiClassifier getClassifier() {
		return classifier;
	}

	@Override
	public SVMMultiClassifier train(InstanceList trainingSet) {
		List<ru.iitp.proling.ml.core.Instance> instances = new ArrayList<ru.iitp.proling.ml.core.Instance>();
		int[] targets = new int[trainingSet.size()];
		
		for(int i = 0; i != trainingSet.size(); i++){
			Instance inst = trainingSet.get(i);
			instances.add(new ru.iitp.proling.ml.core.MalletInstance((SparseVector)inst.getData()));
			targets[i] = ((Label)inst.getTarget()).getIndex();
		}
		
		MulticlassProblem p = new MulticlassProblemBasic(instances, targets);
		MultiScorer scorer = solver.solve(p);
		
		classifier = new SVMMultiClassifier(trainingSet.getPipe(), scorer);
		return classifier;


	}

}
