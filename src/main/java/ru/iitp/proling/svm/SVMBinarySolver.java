package ru.iitp.proling.svm;

import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.Scorer;

public abstract class SVMBinarySolver extends BinarySolver {

	public abstract Scorer solve(WeightVector wv);
	

	public static double loss(WeightVector wv) {
		double sum = 0;
		for(int i = 0; i != wv.size(); i++){
			double res = wv.target(i) * wv.dot(i);
			sum += Math.min(1.0, Math.max(0.0, 1.0 - res));
		  }
		return sum/wv.size();
	}


	public static double objectiveDual(WeightVector wv) {
	double res = wv.snorm()/2;
		  
	for(int i = 0; i != wv.size(); i++)
		res -= wv.alpha(i);

	return res;
	}
	
	public static double objectiveDualL2(WeightVector wv) {
		double res = wv.snorm()/2;
			  
		for(int i = 0; i != wv.size(); i++)
			res -= wv.alpha(i);

		return res;
		}


	public static double zero_one_loss(WeightVector wv) {
		int misclassified = 0;
		
		for(int i =0; i != wv.size(); i++)
			if(wv.target(i) * wv.dot(i) < 0)
				misclassified++;
		
		return 1.0*(misclassified)/wv.size();
	}



}
