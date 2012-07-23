package ru.iitp.proling.ml.boosting;

import java.util.List;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.scorer.BinaryStumpScorer;
import ru.iitp.proling.ml.scorer.Scorer;

public class BinaryStump implements WeakLearner {

	@Override
	public Scorer train(List<Instance> dataset, double[] targets, double[] weights) {
		int dim = 0;
		
		for(int i = 0; i != weights.length; i++){
			dim = Math.max(dim, dataset.get(i).dim());
		}
		return makeStump(dataset, targets, dim + 1, weights);
		
	}
	
	public Scorer makeStump(List<Instance> dataset, double[] targets, int dim, double[] weights){
		
		double[] positive = new double[dim];
		double[] negative = new double[dim];
		double pos = 0;
		double neg = 0;
		
		for(int i = 0; i != dataset.size(); i++){
			
			if(targets[i] == 1.0)
				pos += weights[i];
			else
				neg += weights[i];
			
			Instance x = dataset.get(i);
			for(int j = 0; j != x.size(); j++){
				if(targets[i] == 1.0)
					positive[x.indexAt(j)]+= weights[i];
				else
					negative[x.indexAt(j)]+= weights[i];
			}
		}
		
		
		
		int best = 0;
		double err = 0.0;
		
		for(int i = 1; i != dim; i++){
			double n_err = pos - positive[i] + negative[i];
			double e_raw = 1.0*n_err;
			double e = Math.abs(e_raw - 0.5);
			
			if( e > err){
				best = i;
				err = e;
			}
		}
		
		return new BinaryStumpScorer(best);
	}

}
