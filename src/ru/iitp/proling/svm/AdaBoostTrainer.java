package ru.iitp.proling.svm;

import java.util.Arrays;
import gnu.trove.TDoubleArrayList;
import java.util.List;
import java.util.ArrayList;

import ru.iitp.proling.svm.kernel.Kernel;

public class AdaBoostTrainer {
	// classifier is sign(sum(alpha(i)*<vec_i, sample>))
	protected TDoubleArrayList alpha;
	protected List<double[]> vecs;
	protected int iter;
	protected Kernel kernel;
	
	
	public AdaBoostTrainer(int iter, Kernel kernel){		
		alpha = new TDoubleArrayList();
		vecs = new ArrayList<double[]>();
		this.kernel = kernel;
		this.iter = iter;
	}
	
	public void train(WeightVector wv, BinarySolver solver){
		
		double[] D = new double[wv.size()];
		double m = wv.size();
		
		
		Arrays.fill(D, 1/m);
		
		for(int t = 0; t != iter; t++){
						
			WeightVectorCost w = new WeightVectorCost(wv, D);
			w.clear();
			solver.solve(w);
			
			
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
			vecs.add(Arrays.copyOf(w.vec(), w.vec().length));
		}
	}
	
	public double score(SparseVector vec){
		double res = 0;
		
		
		for(int i = 0; i != alpha.size(); i++){
			res += alpha.get(i) * kernel.dot(vecs.get(i), vec);
		}
		
		return res;
	}
	
	public double zero_one_loss(Dataset ds, double[] targets){
		int misclassified = 0; 
		
		
		for(int i = 0; i != ds.size(); i++)
			if(score(ds.vec(i)) * targets[i] < 0)
				misclassified++;
		
		return 1.0*misclassified/ds.size();
	}
	
	public int size(){
		return alpha.size();
	}

}
