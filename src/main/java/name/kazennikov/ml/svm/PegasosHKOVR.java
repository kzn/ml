package name.kazennikov.ml.svm;

import java.util.Arrays;
import java.util.List;

import name.kazennikov.ml.core.Instance;


public abstract class PegasosHKOVR extends AbstractPegasosOVR {
	
	List<Instance> instances;
	double[] w;
	double[] w_avg;
	double nu;
	double sqnorm;
	int nClasses;

	
	public PegasosHKOVR(List<Instance> instances, int numClasses, int dim,
			int iter, int k, double c, int start_iter) {
		super(iter, k, c, start_iter);
		
		this.instances = instances;
		this.w = new double[dim];
		this.w_avg = new double[dim];
		nu = 1.0;
		this.nClasses = numClasses;
	}

	public abstract int hash(int cls, int index);
	


	@Override
	public void init() {
		Arrays.fill(w, 0.0);
		Arrays.fill(w_avg, 0);
		nu = 1.0;
		sqnorm = 0;
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
	public double dot(int cls, int vec) {
		double z = 0; 
		
		Instance x = instances.get(vec);
		for(int i = 0; i < x.size(); i++) {
			z += w[hash(cls, x.indexAt(i))] * x.valueAt(i);
		}

		return  z * nu;
	}

	@Override
	public void add(int cls, int vec, double factor) {
		
		sqnorm += 2 * factor * dot(cls, vec); // 
		
		Instance x = instances.get(vec);		
		
		for(int i = 0; i < x.size(); i++) {
			double add = factor * x.valueAt(i);
			w[hash(cls, x.indexAt(i))] += add / nu;
			sqnorm += factor * factor * x.valueAt(i) * x.valueAt(i);
		}
	}

	@Override
	public void scale(double factor) {
		this.nu *= factor;
		sqnorm *= factor * factor;
		
		if(this.nu > 1E20 || this.nu < 1E-20) {
			for(int i = 0; i < w.length; i++) {
				w[i] *= nu;
			}
			nu = 1.0;
		}
	}

	@Override
	public double snorm() {
		return sqnorm;
	}
	
	public double[] w() {
		for(int i = 0; i < w.length; i++) {
			w[i] *= nu;
		}
		
		nu = 1.0;
		return w;
	}

}
