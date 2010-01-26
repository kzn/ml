package ru.iitp.proling.svm;

import java.util.Arrays;

import ru.iitp.proling.svm.kernel.Kernel;

public class AdaBoostTrainer {
	// classifier is sign(sum(alpha(i)*<vec_i, sample>))
	protected double[] alpha;
	protected double[][] vecs;
	protected int iter;
	protected Kernel kernel;
	
	
	public AdaBoostTrainer(int iter, Kernel kernel){		
		alpha = new double[iter];
		vecs = new double[iter][];
		this.kernel = kernel;
	}
	
	public void train(Dataset ds, double[] targets, int iter){
		assert(targets.length == ds.size());
		
		double[] D = new double[ds.size()];
		double m = ds.size();
		
		
		Arrays.fill(D, 1/m);
		
		for(int t = 0; t != iter; t++){
			double[] h_targets = Arrays.copyOf(D, targets.length); //targets for weak learning
			
			for(int i = 0; i != h_targets.length; i++)
				h_targets[i] *= ds.size();
		
			WeightVectorLinear wv = new WeightVectorLinear(ds, targets, h_targets, kernel);
			wv.clear();
			DCDSolver.solve(wv, 0.05, 0.05, 500, 0.1, 1000000);
			
			
			
			double e = 0; // e_t
			
			
			
			for(int i = 0; i != wv.size(); i++){
				if(wv.dot(i)*targets[i] < 0)
					e += D[i];
			}
			double a = 0.5 * Math.log((1 - e)/e);
			
			System.out.printf("e[%d]=%f, 1/0 loss:%f, a:%f\n", t, e, wv.zero_one_loss(), a);
			
			for(int i = 0; i != D.length; i++)
				D[i] *= Math.exp(-a * targets[i] * Math.signum(wv.dot(i)));
			
			// norm D[i]
			double sum = 0;
			for(double k: D)
			sum += k;
			
			for(int i = 0; i != D.length; i++)
				D[i] /= sum;
			
			alpha[t] = a;
			vecs[t] = Arrays.copyOf(wv.vec(), wv.vec().length);
		}
	}
	
	public double score(SparseVector vec){
		double res = 0;
		
		for(int i = 0; i != alpha.length; i++){
			res += alpha[i] * kernel.dot(vecs[i], vec);
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

}
