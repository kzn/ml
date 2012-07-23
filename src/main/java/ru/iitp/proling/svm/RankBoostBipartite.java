package ru.iitp.proling.svm;


import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.EnsembleScorer;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.ml.scorer.TrivialScorer;

/**
 * Implementation of RankBoost.B algorithm
 * @author ant
 *
 */
public class RankBoostBipartite {
	
	public static EnsembleScorer train(WeightVector wv, BinarySolver solver, int iter, List<Integer> qids){
		TDoubleArrayList alpha = new TDoubleArrayList();
		List<Scorer> vecs = new ArrayList<Scorer>();
		
		// X_0 is lesser subset
		// X_1 is greater subset
		/* As there there is only 2 distinct subsets:
		 * lesser label is max(wv.targets)
		 * greater label is min(wv.targets)
		 * So, we will find labels, and then compute lesser and greater subsets for algorithm requirements
		 */
		
		double lesser_rank = Double.POSITIVE_INFINITY;
		double greater_rank = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i != wv.size(); i++){
			lesser_rank = Math.min(wv.target(i), lesser_rank);
			greater_rank = Math.max(wv.target(i), greater_rank);
		}
		
		TIntArrayList greater_idx = new TIntArrayList();
		TIntArrayList lesser_idx = new TIntArrayList();
		double[] D = new double[wv.size()];
		
		for(int i = 0; i != wv.size(); i++){
			if(wv.target(i) == lesser_rank)
				lesser_idx.add(i);
			else if(wv.target(i) == greater_rank)
				greater_idx.add(i);
		}
		
		// init nu[i]
		for(int i = 0; i != wv.size(); i++){
			if(wv.target(i) == lesser_rank)
				D[i] = 1.0/lesser_idx.size();
			else if(wv.target(i) == greater_rank)
				D[i] = 1.0/greater_idx.size();
		}
		
		
		
		
		assert(lesser_idx.size() + greater_idx.size() == wv.size());
		
		
		 
		
		
		WeightVectorRanking wr = new WeightVectorRanking(wv, qids);
		
		for(int t = 0; t != iter; t++){
			
			double[] D1 = Arrays.copyOf(D, D.length);
			for(int i = 0; i != D1.length; i++) // norm cost to 1.0 base
				D1[i] *= wv.size();
			
			WeightVectorCost w = new WeightVectorCost(wr, D1);
			w.clear();
			solver.solve(w);
			
			
			double r = 0; // e_t
			
			for(int i = 0; i != wr.size(); i++){
				double dh = wr.dot(i); // h(x_0) - h(x_1) is - w.dot(i); 
				
				if(Math.abs(dh) > 1) // bound it into [-1..1]
					dh /= Math.abs(dh);
				
				r += D[i] * dh;
			}
			
			// terminate if learning is useless
			
			double a = 0.5 * Math.log((1+r)/(1-r));
			
			System.out.printf("r[%d]=%f, 1/0 loss:%f, a:%f\n", t, r, SVMBinarySolver.zero_one_loss(wv), a);
			// normalization factors
			double z_g = 0; 
			double z_l = 0;
			
			for(int i = 0; i != wv.size(); i++){
				if(wv.target(i) == greater_rank){
					D[i] = D[i]*Math.exp(-a*wv.dot(i));
					z_g += D[i];
					}
				else if(wv.target(i) == lesser_rank){
					D[i] = D[i]*Math.exp(a*wv.dot(i));
					z_l += D[i];
				}
			}
			
			for(int i = 0; i != D.length; i++){
				if(wv.target(i) == greater_rank)
					D[i] /= z_g;
				else if(wv.target(i) == lesser_rank)
					D[i] /= z_l;
			}
			
			alpha.add(a);
			vecs.add(new TrivialScorer(w.vec(), w.kernel()));
		}
		
		return new EnsembleScorer(vecs, alpha);
	}

}
