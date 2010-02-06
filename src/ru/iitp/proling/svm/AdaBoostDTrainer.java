package ru.iitp.proling.svm;

import gnu.trove.TDoubleArrayList;
import gnu.trove.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.iitp.proling.svm.kernel.Kernel;

public class AdaBoostDTrainer{
	
	public static EnsembleScorer train(WeightVector wv, BinarySolver solver, int iter, double threshold){
		TDoubleArrayList alpha = new TDoubleArrayList();
		List<Scorer> vecs = new ArrayList<Scorer>();
		
		double[] D = new double[wv.size()]; 
		//double[] D1 = new double[D.length];
		double m = wv.size();
		
		Arrays.fill(D, 1/m);
		
		for(int t = 0; t != iter; t++){
			
			double[] D1 = Arrays.copyOf(D, D.length);
			TIntArrayList samples = new TIntArrayList();
			
			for(int i = 0; i != D1.length; i++){
				D1[i] *= m;
				if(D1[i] >= threshold)
					samples.add(i);
			}
			
			WeightVectorCost w = new WeightVectorCost(wv, D1);
			WeightVectorSlice w_slice = new WeightVectorSlice(w);
			
			for(int i = 0; i != samples.size(); i++)
				w_slice.add_vec(i);
			
			System.out.println("Active samples:" + w_slice.size());
				
			
			w_slice.clear();
			Scorer s = solver.solve(w_slice);
			
			
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
		
		return new EnsembleScorer(vecs, alpha);
	}


}