package ru.iitp.proling.svm;

import ru.iitp.proling.common.ArrayUtils;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.ml.scorer.TrivialScorer;

public class Perceptron extends BinarySolver {
	
	double alpha;
	int iter;

	public Perceptron(double alpha, int iter){
		this.alpha = alpha;
		this.iter = iter;
	}

	@Override
	public Scorer solve(WeightVector wv) {
		int[] indexes = new int[wv.size()];

		for(int i = 0; i != wv.size(); i++)
			indexes[i] = i;
		long start = System.nanoTime();
		for(int t = 0; t != iter; t++){
			int errs = 0;
			ArrayUtils.shuffle(indexes, indexes.length);
			for(int i = 0; i != wv.size(); i++){
				int idx = indexes[i];
				double loss = wv.target(idx) * wv.dot(idx);
				if(loss < 0)
					wv.add(idx, alpha * wv.cost(idx));
			}
			//System.out.printf("Iteration %d, number of errors:%d%n", t, errs);

		}
		System.out.printf("Elapsed on perceptron training: %f secs%n", (System.nanoTime() - start)/1.0E9);

		return new TrivialScorer(wv.vec(), wv.kernel());
	}



}
