package ru.iitp.proling.svm;

public class PolyKernel extends Kernel {
	protected double g;
	protected double c;
	protected int n;
	protected LinearKernel lk;
	
	protected double  sqrt_2cg;
	protected double sqrt_2_g;
	
	public PolyKernel(double g, double c, int n){
		this.g = g;
		this.c = c;
		this.n = n;
		sqrt_2cg = Math.sqrt(2*c*g);
		sqrt_2_g = Math.sqrt(2)*g;
		lk = new LinearKernel();
	}

	@Override
	public int dim(int n) {
		return (n+2)*(n+1)/2;
	}

	@Override
	public double dot(SparseVector x, SparseVector y) {
		// TODO Auto-generated method stub
		double res = g*lk.dot(x, y) + c;
		return res * res;
	}

	@Override
	public double dot(double[] dense, SparseVector x) {
		double sum = 0;
		sum += dense[0]*c;
		for(int i = 0; i != x.size(); i++){
			double tmp = 0;
			int base = x.indexes[i] * (2*n - x.indexes[i] + 1)/2;
			for(int j = i + 1; j != x.size(); j++)
				tmp += dense[base + x.indexes[j]]*x.values[j];

			tmp *= sqrt_2_g;
			tmp += dense[base + x.indexes[i]]*x.values[i] * g;
			tmp += dense[x.indexes[i]]*sqrt_2cg;
			sum += tmp*x.values[i];
		}
		return sum;

	}

	@Override
	public SparseVector pipe(SparseVector x) {
		return null;
	}

	@Override
	public void add(double[] dense, SparseVector x, double factor) {
		dense[0]+= factor * c;
		for(int i = 0; i != x.size(); i++){
			int base = x.indexes[i] * (2*n - x.indexes[i] + 1)/2;
			double tmp = factor * x.values[i];
	        dense[base + x.indexes[i]] += g*tmp*x.values[i];
		      
		      for(int j = i+1; j != x.size(); j++){
		    	  dense[base + x.indexes[j]] += Math.sqrt(2) * g * tmp * x.values[j];
		      }

		      dense[x.indexes[i]] += tmp*Math.sqrt(2*c*g);
		    }
	}

	@Override
	public double snorm(SparseVector x) {
		// TODO Auto-generated method stub
		double res = g * lk.snorm(x) + c;
		return res * res;
	}

}
