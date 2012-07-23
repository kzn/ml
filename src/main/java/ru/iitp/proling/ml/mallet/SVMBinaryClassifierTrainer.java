package ru.iitp.proling.ml.mallet;

import java.util.ArrayList;
import java.util.List;

import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.WeightVectorMallet;
import ru.iitp.proling.svm.kernel.Kernel;

import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.SparseVector;

public class SVMBinaryClassifierTrainer extends ClassifierTrainer<SVMBinaryClassifier> {
	SVMBinaryClassifier classifier;
	double cPos;
	double cNeg;
	double eps;
	int threshold;
	int maxIter;
	Kernel kernel;
	
	public SVMBinaryClassifierTrainer(double cPos, double cNeg, double eps, int maxIter, int threshold, Kernel kernel){
		this.cPos = cPos;
		this.cNeg = cNeg;
		this.eps = eps;
		this.threshold = threshold;
		this.maxIter = maxIter;
		this.kernel = kernel;
	}

	@Override
	public SVMBinaryClassifier getClassifier() {
		return classifier;
	}

	@Override
	public SVMBinaryClassifier train(InstanceList trainingSet) {
		
		if(trainingSet.getTargetAlphabet().size() > 2)
			throw new IllegalArgumentException("Training set has more than 2 classes. Illegal for binary SVM problem");
		
		List<ru.iitp.proling.ml.core.Instance> instances = new ArrayList<ru.iitp.proling.ml.core.Instance>();
		double[] targets = new double[trainingSet.size()];
		
		for(int i = 0; i != trainingSet.size(); i++){
			Instance inst = trainingSet.get(i);
			instances.add(new ru.iitp.proling.ml.core.MalletInstance((SparseVector)inst.getData()));
			targets[i] = ((Label)inst.getTarget()).getIndex() == 1? 1 : -1;
		}
		
		WeightVector wv = new WeightVectorMallet(instances, targets, kernel);
		Scorer scorer = DCDSolver.solve(wv, cPos, cPos, maxIter, eps , threshold, 1);
		
		classifier = new SVMBinaryClassifier(trainingSet.getPipe(), scorer);
		return classifier;
	}

}
