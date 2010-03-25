package ru.iitp.proling.svm;

import gnu.trove.TObjectDoubleHashMap;
import ru.iitp.proling.common.Alphabet;
import ru.iitp.proling.ml.core.Classifier;
import ru.iitp.proling.ml.core.Dataset;
import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.svm.kernel.Kernel;

public class SVMClassifierDCD<T> implements Classifier<T>{
	double[] vec;
	Alphabet<T> alphabet;
	Kernel kernel;
	double c;
	double[] costs;
	
	
	
	public SVMClassifierDCD(Kernel kernel, double c, double[] costs){
		this.kernel = kernel;
		this.c = c;
		this.costs = costs;
	}
	
	@Override
	public T classify(Instance<?> vec) {
		return kernel.dot(this.vec, vec) > 0? alphabet.get(1) : alphabet.get(2); 
	}

	@Override
	public TObjectDoubleHashMap<T> scores(Instance<?> v) {
		TObjectDoubleHashMap<T> hm = new TObjectDoubleHashMap<T>(alphabet.size());
		double score = kernel.dot(this.vec, v);
		hm.put(alphabet.get(1), score);
		hm.put(alphabet.get(1), -score);
		return hm;
	}
	
	
	
	@Override
	public void build(Dataset<T> dataset) {
		if(dataset.alphabet().size() > 2)
			throw new IllegalArgumentException("Datasets for classification must have at most 2 labels");
		
		alphabet = dataset.alphabet();
		
		double[] targets = new double[dataset.size()];
		
		for(int i = 0; i != dataset.size(); i++){
			int idx = alphabet.get(dataset.get(i).value());
			targets[i] = idx == 1? 1 : -1;
		}
		
		WeightVector wv = new WeightVectorLinear(dataset, targets, kernel);
		DCDSolver.solve(wv, c, c, 500, 0.1, 100000);
		vec = wv.vec();
	}


}
