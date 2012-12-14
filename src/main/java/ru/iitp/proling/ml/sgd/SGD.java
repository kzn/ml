package ru.iitp.proling.ml.sgd;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.List;

import ru.iitp.proling.ml.core.Instance;

public class SGD {
	LossFunction loss;
	double  lambda;
	double  eta0;
	double[] w;
	double  wDivisor;
	double  wBias;
	int  t;
	boolean regularizedBias;
	boolean bias;
	int dim; 
	
	public SGD(int dim, double lambda, double eta0) {
		this.dim = dim;
		this.lambda = lambda;
		this.eta0 = eta0;
		this.w = new double[dim];
		wDivisor = 1.0;
		t = 0;
	}
	
	public void renorm() {
		if(wDivisor != 1.0) {
			DenseVector.scale(w, 1.0/wDivisor);
			wDivisor = 1.0;
		}
	}
	
	public double wNorm() {
		double norm = DenseVector.dot(w,w) / wDivisor / wDivisor;
		if(regularizedBias)
		  norm += wBias * wBias;

		  return norm;
	}
	
	/// Perform one iteration of the SGD algorithm with specified gains
	public void trainOne(Instance x, double y, double eta)
	{
		double s = DenseVector.dot(w, x) / wDivisor + wBias;
		// update for regularization term
		wDivisor = wDivisor / (1 - eta * lambda);
		if (wDivisor > 1e5) 
			renorm();
		// update for loss term
		double d = loss.dloss(s, y);
		if (d != 0)
			DenseVector.add(w, x, eta * d * wDivisor);
		// same for the bias
		if(bias) {
			double etab = eta * 0.01;
			if(regularizedBias)
				wBias *= (1 - etab * lambda);
			wBias += etab * d;
		}

	}


	/// Perform a training epoch
	void train(int imin, int imax, List<Instance> xp, TDoubleArrayList yp) {

		assert(imin <= imax);
		assert(eta0 > 0);
		for (int i = imin; i < imax; i++)
		{
			double eta = eta0 / (1 + lambda * eta0 * t);
			trainOne(xp.get(i), yp.get(i), eta);
			t += 1;
		}
	}


}
