package name.kazennikov.ml.svm;

import java.util.Arrays;
import java.util.List;

import name.kazennikov.ml.core.Instance;


public abstract class DCDMCLinearHK extends AbstractDCDMC {
	List<Instance> instances;
	int[] targets;
	double[] w;
	int numClasses;
	int dim;
	
	public DCDMCLinearHK(List<Instance> instances, int[] targets, int dim, int numClasses, 
			double c_pos, double c_neg, int iter, double eps, int threshold) {
		super(c_pos, c_neg, iter, eps, threshold);
		this.instances = instances;
		this.targets = targets;
		w = new double[dim];
		this.numClasses = numClasses;
	}
	
	public abstract int h(int c, int index);

	

	@Override
	public void init() {
		Arrays.fill(w, 0.0);
	}

	@Override
	public int size() {
		return instances.size();
	}

	@Override
	public int numClasses() {
		return numClasses;
	}

	@Override
	public double target(int c, int i) {
		return targets[i] == c? 1.0 : -1.0;
	}


	@Override
	public double snorm(int vec) {
		Instance v = instances.get(vec);
		double snorm = 0;
		for(int i = 0; i != v.size(); i++) {
			snorm += v.valueAt(i) * v.valueAt(i);
		}

		return snorm;
	}
	
	@Override
	public double dot(int c, int vec) {
		Instance v = instances.get(vec);
		double val = 0;
		
		for(int i = 0; i != v.size(); i++) {
			val += w[h(c, v.indexAt(i))] * v.valueAt(i);
		}

		return val;
	}


	@Override
	public void add(int c, int vec, double factor) {
		
		Instance v = instances.get(vec);
		
		for(int i = 0; i != v.size(); i++) {
			w[h(c, v.indexAt(i))] += factor * v.valueAt(i);
		}
	}
	
	public double[] w() {
		return w;
	}
	
	public double zero_one_loss() {
		double loss = 0;
		
		for(int i = 0; i != size(); i++) {
			double score = Double.NEGATIVE_INFINITY;
			int cls = 0;
			
			for(int c = 0; c < numClasses(); c++) {
				double y = dot(c, i);
				if(y > score) {
					score = y;
					cls = c;
				}
			}
			
			if(cls != targets[i])
				loss++;
		}
		
		return loss / size();
	}


		
}
