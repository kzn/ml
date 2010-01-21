package ru.iitp.proling.svm.kernel;

import java.util.TreeMap;

import ru.iitp.proling.svm.SparseVector;

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
		
		double sum = this.wGetter(w, 0)*c;	// bias
		for(int i = 0; i != x.size(); i++){
			double tmp_value = 0;
			long base = (long)x.indexes[i] * (2*n - x.indexes[i] + 1)/2;
			
			for(int j = i + 1; j != x.size(); j++){
				tmp_value += wGetter(w, base + x.indexes[j])*x.values[j];
			}
			tmp_value *= sqrt2_g;
			tmp_value += wGetter(w, base + x.indexes[i])*x.values[i]*g;
			tmp_value += wGetter(w, x.indexes[i])*sqrt2cg;
			sum += tmp_value*x.values[i];
		}

		return sum;

	}

	@Override
	public TreeMap<Long, Double> pipe(SparseVector x) {
		TreeMap<Long, Double> tm = new TreeMap<Long, Double>();
		tm.put(0L, c);
		
		for(int i = 0; i != x.size(); i++){
			double tmp_value = x.values[i];
			long base = (long)x.indexes[i] * (2*n - x.indexes[i] + 1)/2;
			tm.put(base + x.indexes[i], g*tmp_value*x.values[i]);
			for(int j = i + 1; j != x.size(); j++){
			    tm.put(base + x.indexes[j], sqrt2_g*tmp_value*x.values[j]);
			}
			tm.put((long)x.indexes[i], tmp_value*sqrt2cg);
		}
		
		return tm;
	}

	@Override
	public void add(double[] w, SparseVector x, double factor) {
		wAdder(w, 0, factor * c);
		for(int i = 0; i != x.size(); i++){
			double tmp_value = factor*x.values[i];
			long base = (long)x.indexes[i] * (2*n - x.indexes[i] + 1)/2;
			wAdder(w, base + x.indexes[i], g*tmp_value*x.values[i]);
			for(int j = i + 1; j != x.size(); j++){
			    wAdder(w, base + x.indexes[j], sqrt2_g*tmp_value*x.values[j]);
			}
			wAdder(w, x.indexes[i], tmp_value*sqrt2cg);
		}

	}

	@Override
	public double snorm(SparseVector x) {
		// TODO Auto-generated method stub
		double res = g * lk.snorm(x) + c;
		return res * res;
	}

}
