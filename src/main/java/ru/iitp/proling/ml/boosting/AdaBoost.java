package ru.iitp.proling.ml.boosting;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import name.kazennikov.ml.core.Instance;

import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.DiscreteScorer;
import ru.iitp.proling.ml.scorer.EnsembleScorer;
import ru.iitp.proling.ml.scorer.Scorer;


public class AdaBoost{
	int iter;
	WeakLearner weakLearner;
	
	public AdaBoost(WeakLearner weakLearner, int iter){
		this.weakLearner = weakLearner;
		this.iter = iter;
	}
	
	//public abstract Scorer weakLearn(List<Instance> instances, double[] targets, double[] weights);
	
	public EnsembleScorer train(List<Instance> instances, double[] targets){
		double[] weights = new double[instances.size()];
		Arrays.fill(weights, 1.0);
		
		return train(instances, targets, weights);
	}
	
	public EnsembleScorer train(List<Instance> instances, double[] targets, double wPos, double wNeg){
		double[] weights = new double[instances.size()];
		for(int i = 0; i != weights.length; i++)
			weights[i] = targets[i] == 1.0? wPos : wNeg;
		return train(instances, targets, weights);
	}
	
	public EnsembleScorer train(List<Instance> instances, double[] targets, double[] D){
		TDoubleArrayList alpha = new TDoubleArrayList();
		List<Scorer> vecs = new ArrayList<Scorer>();
		double[] scores = new double[instances.size()];

		
		for(int t = 0; t != iter; t++){

			// norm D[i]
			double sum = 0;
			for(double k: D)
				sum += k;
			
			for(int i = 0; i != D.length; i++)
				D[i] /= sum;
						
			Scorer s = weakLearner.train(instances, targets, D);
			for(int i = 0; i != instances.size(); i++)
				scores[i] = Math.signum(s.score(instances.get(i))); // scores[i] = {-1, 1}
			
			
			double e = 0;
			int err = 0;

			
			for(int i = 0; i != instances.size(); i++){
				if(scores[i]*targets[i] < 0){
					e += D[i];
					err++;
				}
			}
			
			double a = 0.5 * Math.log((1 - e)/e);
			
			System.out.printf("e[%d]=%f, a=%f 1/0 errs:%d/%d%n", t, e, a, err, instances.size());
			
			for(int i = 0; i != D.length; i++)
				D[i] *= Math.exp(-a * targets[i] * scores[i]);
			
			
			alpha.add(a);
			vecs.add(new DiscreteScorer(s, 0, 1, -1));
		}
		
		return new EnsembleScorer(vecs, alpha);
	}
}
