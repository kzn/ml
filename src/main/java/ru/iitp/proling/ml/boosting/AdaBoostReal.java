package ru.iitp.proling.ml.boosting;


import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.List;

import name.kazennikov.ml.core.Instance;

import ru.iitp.proling.ml.scorer.EnsembleScorer;
import ru.iitp.proling.ml.scorer.Scorer;
/**
 * Real AdaBoost h(x) -> [-1..1]
 * @author ant
 *
 */
public class AdaBoostReal extends AdaBoost{

	public AdaBoostReal(WeakLearner weakLearner, int iter) {
		super(weakLearner, iter);
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
				scores[i] = s.score(instances.get(i));
			
			
			double r = 0;
			int err = 0;

			
			for(int i = 0; i != instances.size(); i++){
				r += D[i]*scores[i]*targets[i];			
			}
			
			double a = 0.5 * Math.log((1 + r)/(1 - r));
			
			System.out.printf("r[%d]=%f, a=%f 1/0 errs:%d/%d%n", t, r, a, err, instances.size());
			
			for(int i = 0; i != D.length; i++)
				D[i] *= Math.exp(-a * targets[i] * scores[i]);
			
			
			alpha.add(a);
			vecs.add(s);
		}
		
		return new EnsembleScorer(vecs, alpha);
	}

}
