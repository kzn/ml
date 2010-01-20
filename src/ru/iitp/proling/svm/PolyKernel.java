package ru.iitp.proling.svm;

public class PolyKernel extends Kernel {
	protected double g;
	protected double c;
	protected int n;
	protected LinearKernel lk;
	
	protected double  sqrt2cg;
	protected double sqrt2_g;
	
	public PolyKernel(double g, double c, int n){
		this.g = g;
		this.c = c;
		this.n = n;
		sqrt2cg = Math.sqrt(2*c*g);
		sqrt2_g = Math.sqrt(2)*g;
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
	public double dot(double[] w, SparseVector x) {
		
		double sum = w[0]*c;	// bias
		for(int i = 0; i != x.size(); i++){
			double tmp_value = 0;
			int base = x.indexes[i] * (2*n - x.indexes[i] + 1)/2;
			
			for(int j = i + 1; j != x.size(); j++){
				tmp_value += w[base + x.indexes[j]]*x.values[j];
			}
			tmp_value *= sqrt2_g;
			tmp_value += w[base + x.indexes[i]]*x.values[i]*g;
			tmp_value += w[x.indexes[i]]*sqrt2cg;
			sum += tmp_value*x.values[i];
		}

		return sum;

	}

	@Override
	public SparseVector pipe(SparseVector x) {
		return null;
	}

	@Override
	public void add(double[] w, SparseVector x, double factor) {
		w[0] += factor* c;
		for(int i = 0; i != x.size(); i++){
			double tmp_value = factor*x.values[i];
			int base = x.indexes[i] * (2*n - x.indexes[i] + 1)/2;
			w[base + x.indexes[i]] += g*tmp_value*x.values[i];
			for(int j = i + 1; j != x.size(); j++){
			    w[base + x.indexes[j]] += sqrt2_g*tmp_value*x.values[j];
			}
			w[x.indexes[i]] += tmp_value*sqrt2cg;
		}

	}

	@Override
	public double snorm(SparseVector x) {
		// TODO Auto-generated method stub
		double res = g * lk.snorm(x) + c;
		return res * res;
	}

}
