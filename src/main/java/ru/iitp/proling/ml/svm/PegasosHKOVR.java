package ru.iitp.proling.ml.svm;

import java.util.Arrays;
import java.util.List;

import ru.iitp.proling.ml.core.Instance;

public abstract class PegasosHKOVR extends AbstractPegasosOVR {
	
	List<Instance> instances;
	int[] targets;
	double[] w;
	double f;
	double sqnorm;
	int nClasses;

	
	public PegasosHKOVR(List<Instance> instances, int[] targets, int numClasses, int dim,
			int iter, int k, double c, int start_iter) {
		super(iter, k, c, start_iter);
		
		this.instances = instances;
		this.targets = targets;
		this.w = new double[dim];
		f = 1.0;
		this.nClasses = numClasses;
	}

	public abstract int hash(int cls, int index);
	


	@Override
	public void init() {
		Arrays.fill(w, 0.0);
		f = 1.0;
	}

	@Override
	public int size() {
		return instances.size();
	}

	@Override
	public int numClasses() {
		return nClasses;
	}

	@Override
	public double target(int cls, int vec) {
		return targets[vec] == cls? 1.0 : -1.0;
	}

	@Override
	public double dot(int cls, int vec) {
		double z = 0; 
		
		Instance x = instances.get(vec);
		for(int i = 0; i < x.size(); i++) {
			z += w[hash(cls, x.indexAt(i))] * x.valueAt(i);
		}

		return  z * f;
	}

	@Override
	public void add(int cls, int vec, double factor) {
		
		Instance x = instances.get(vec);
		for(int i = 0; i < x.size(); i++) {
			double add = factor * x.valueAt(i);
			w[hash(cls, x.indexAt(i))] += add / f;
			sqnorm += add + factor * factor * x.valueAt(i) * x.valueAt(i);
		}
	}

	@Override
	public void scale(double factor) {
		this.f *= factor;
		sqnorm *= factor * factor;
		
		if(this.f > 1E6 || this.f < 1E-6) {
			for(int i = 0; i < w.length; i++) {
				w[i] *= f;
			}
			f = 1.0;
		}
	}

	@Override
	public double snorm() {
		return Math.sqrt(sqnorm);
	}
	
	public double[] w() {
		return w;
	}

}
