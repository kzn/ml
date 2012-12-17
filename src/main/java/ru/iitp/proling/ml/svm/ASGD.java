package ru.iitp.proling.ml.svm;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.Arrays;
import java.util.List;

import ru.iitp.proling.ml.core.Instance;


public class ASGD {
	
	double wDivisor = 1.0;
	double aDivisor = 1.0;
	double wFraction = 1;
	
	double[] w; // current weight
	double[] a; // surrogate average
	
	double eta0 = 0.001;
	double lambda = 1.0;
	
	int tstart; // iteration when start the averaging
	int t;
	double mu0; // averaging rate
	
	void scale(double[] v, double factor) {
		for(int i = 0; i < v.length; i++) {
			w[i] *= factor;
		}
	}
	
	void combine(double[] w1, double f1, double[] w2, double f2) {
		for(int i = 0; i < w1.length; i++) {
			w1[i] = w1[i] * f1 + w2[i] * f2;
		}
	}
	
	double dloss(double predicted, double ref) {
		double z = predicted * ref;
	    if (z > 1) 
	      return 0;
	    return predicted;
	}
	
	
	/// Renormalize the weights
	void renorm() {
	  if (wDivisor != 1.0 || aDivisor != 1.0 || wFraction != 0) {
	      combine(a, 1/aDivisor, w, wFraction/aDivisor); // a = a * (1/aDivisor) + w * (wFraction/aDivisor)
	      scale(w, 1/wDivisor);
	      wDivisor = aDivisor = 1;
	      wFraction = 0;
	    }
	}
	
	double dot(double[] x, double[] y) {
		double res = 0;
		for(int i  = 0; i < x.length; i++) {
			res += x[i] * y[i];
		}
		return res;
	}

	/// Compute the norm of the normal weights
	double wnorm() {
	  double norm = dot(w,w) / wDivisor / wDivisor;
	  return norm;
	}

	/// Compute the norm of the averaged weights
	double anorm() {
	  renorm(); // this is simpler!
	  double norm = dot(a,a);
	  return norm;
	}
	
	double dot(double[] w, Instance x) {
		double ret = 0;
		
		for(int i = 0; i < x.size(); i++) {
			ret += w[x.indexAt(i)] *x.valueAt(i);
		}
		
		return ret;
	}
	
	void add(double[] w, Instance x, double factor) {
		for(int i = 0; i < x.size(); i++) {
			w[x.indexAt(i)] += x.valueAt(i) * factor;
		}
	}
	
	void clear(double[] w) {
		Arrays.fill(w, 0);
	}
	
	/**
	Learn (x,y) with rate eta and averaging factor mu.

	if(mu >= 1.0) {
		a = w;
	}

	@param x training sample
	@param y training label
	@param eta current learning rate
	@param mu averaging factor 
	*/
	void trainOne(Instance x, double y, double eta, double mu) {
	  // Renormalize if needed
	  if (aDivisor > 1e5 || wDivisor > 1e5) 
		  renorm();

	  // Forward
	  double s = dot(w, x) / wDivisor;
	  // SGD update for regularization term
	  wDivisor = wDivisor / (1 - eta * lambda);
	  // SGD update for loss term
	  double d = dloss(s, y);
	  double etd = eta * d * wDivisor; // update size

	  if (etd != 0) {
	    add(w, x, etd);
	  }
	  
	  // Averaging
	  if (mu >= 1) {
	      clear(a);
	      aDivisor = wDivisor;
	      wFraction = 1;
	  } else if (mu > 0) {
	 
		  if (etd != 0) {
			  add(a, x, - wFraction * etd);
		  }

	      aDivisor = aDivisor / (1 - mu);
	      wFraction = wFraction + mu * aDivisor / wDivisor;
	    }
	}


	/// Perform a training epoch
	void train(int imin, int imax, List<Instance> x, TDoubleArrayList y) 
	{
		System.out.printf("Training on [%d,%d].%n", imin, imax);
		assert(imin <= imax);
		assert(eta0 > 0);

		for (int i = imin; i <= imax; i++) {
			double eta = eta0 / Math.pow(1 + lambda * eta0 * t, 0.75); // effective learning rate
			double mu = (t <= tstart) ? 1.0 : mu0 / (1 + mu0 * (t - tstart));
			trainOne(x.get(i), y.get(i), eta, mu);
			t += 1;
		}

		System.out.printf("wNorm=%f aNorm=%f%n", wnorm(), anorm());
	}

}
