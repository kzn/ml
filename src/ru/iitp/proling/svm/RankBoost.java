package ru.iitp.proling.svm;

import gnu.trove.TDoubleArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.iitp.proling.svm.kernel.Kernel;
import ru.iitp.proling.svm.kernel.LinearKernel;

// N.B. It is a pair-wise algorithm, so we can use WeightVectorRanking problem formulation
public class RankBoost {
	// classifier is sign(sum(alpha(i)*<vec_i, sample>))
	
	public static EnsembleScorer train(WeightVectorRanking wv, BinarySolver solver, int iter){
		TDoubleArrayList alpha = new TDoubleArrayList();
		List<Scorer> vecs = new ArrayList<Scorer>();
		
		
		double[] D = new double[wv.size()]; 
		double m = wv.size();
		
		Arrays.fill(D, 1/m);
		
		for(int t = 0; t != iter; t++){
			
			double[] D1 = Arrays.copyOf(D, D.length);
			for(int i = 0; i != D1.length; i++)
				D1[i] *= m;
			
			WeightVectorCost w = new WeightVectorCost(wv, D1);
			w.clear();
			solver.solve(w);
			
			
			double r = 0; // e_t
			
			for(int i = 0; i != wv.size(); i++){
				double dh = w.dot(i); // h(x_0) - h(x_1) is - w.dot(i); 
				
				if(Math.abs(dh) > 1) // bound it into [-1..1]
					dh /= Math.abs(dh);
				
				r += D[i] * dh;
			}
			
			// terminate if learning is useless
			
			double a = 0.5 * Math.log((1+r)/(1-r));
			
			System.out.printf("r[%d]=%f, 1/0 loss:%f, a:%f\n", t, r, w.zero_one_loss(), a);
			
			for(int i = 0; i != D.length; i++)
				D[i] *= Math.exp(-a *  w.dot(i));
			
			// norm D[i]
			double sum = 0;
			for(double k: D)
			sum += k;
			
			for(int i = 0; i != D.length; i++)
				D[i] /= sum;
			
			alpha.add(a);
			vecs.add(new TrivialScorer(w.vec(), w.kernel()));
		}
		
		return new EnsembleScorer(vecs, alpha);
	}
}
