package ru.iitp.proling.svm;

import java.util.Arrays;
import gnu.trove.TDoubleArrayList;
import java.util.List;
import java.util.ArrayList;

import ru.iitp.proling.svm.kernel.Kernel;
import ru.iitp.proling.svm.kernel.LinearKernel;

public class AdaBoost {
	public static EnsembleScorer train(WeightVector wv, BinarySolver solver, int iter){
		TDoubleArrayList alpha = new TDoubleArrayList();
		List<Scorer> vecs = new ArrayList<Scorer>();

		
		double[] D = new double[wv.size()]; 
		//double[] D1 = new double[D.length];
		double m = wv.size();
		
		Arrays.fill(D, 1/m);
		
		for(int t = 0; t != iter; t++){
			
			double[] D1 = Arrays.copyOf(D, D.length);
			for(int i = 0; i != D1.length; i++)
				D1[i] *= m;
			
			WeightVectorCost w = new WeightVectorCost(wv, D1);
			w.clear();
			Scorer s = solver.solve(w);
			
			
			double e = 0; // e_t
			
			for(int i = 0; i != wv.size(); i++){
				if(w.dot(i)*w.target(i) < 0)
					e += D[i];
			}
			
			// terminate if learning is useless
			if(e > 0.5)
				break;
			
			double a = 0.5 * Math.log((1 - e)/e);
			
			System.out.printf("e[%d]=%f, 1/0 loss:%f, a:%f\n", t, e, w.zero_one_loss(), a);
			
			for(int i = 0; i != D.length; i++)
				D[i] *= Math.exp(-a * w.target(i) * Math.signum(w.dot(i)));
			
			// norm D[i]
			double sum = 0;
			for(double k: D)
			sum += k;
			
			for(int i = 0; i != D.length; i++)
				D[i] /= sum;
			
			alpha.add(a);
			vecs.add(s);
		}
		
		return new EnsembleScorer(vecs, alpha, wv.kernel());
	}
}
