package ru.iitp.proling.svm.kernel;

import java.io.Serializable;
import java.util.TreeMap;

import cc.mallet.types.SparseVector;

import ru.iitp.proling.ml.core.Instance;

public class PolyKernel extends Kernel implements Serializable{
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
	public double dot(Instance x, Instance y) {
		// TODO Auto-generated method stub
		double res = g*lk.dot(x, y) + c;
		return res * res;
	}

	@Override
	public double dot(double[] w, Instance x) {
		
		double sum = this.wGetter(w, 0)*c;	// bias
		for(int i = 0; i != x.size(); i++){
			double tmp_value = 0;
			long base = (long)x.indexAt(i) * (2*n - x.indexAt(i) + 1)/2;
			
			for(int j = i + 1; j != x.size(); j++){
				tmp_value += wGetter(w, base + x.indexAt(j))*x.valueAt(j);
			}
			tmp_value *= sqrt2_g;
			tmp_value += wGetter(w, base + x.indexAt(i))*x.valueAt(i)*g;
			tmp_value += wGetter(w, x.indexAt(i))*sqrt2cg;
			sum += tmp_value*x.valueAt(i);
		}

		return sum;

	}

	@Override
	public TreeMap<Long, Double> pipe(Instance x) {
		TreeMap<Long, Double> tm = new TreeMap<Long, Double>();
		tm.put(0L, c);
		
		for(int i = 0; i != x.size(); i++){
			double tmp_value = x.valueAt(i);
			long base = (long)x.indexAt(i) * (2*n - x.indexAt(i) + 1)/2;
			tm.put(base + x.indexAt(i), g*tmp_value*x.valueAt(i));
			for(int j = i + 1; j != x.size(); j++){
			    tm.put(base + x.indexAt(j), sqrt2_g*tmp_value*x.valueAt(j));
			}
			tm.put((long)x.indexAt(i), tmp_value*sqrt2cg);
		}
		
		return tm;
	}

	@Override
	public void add(double[] w, Instance x, double factor) {
		wAdder(w, 0, factor * c);
		for(int i = 0; i != x.size(); i++){
			double tmp_value = factor*x.valueAt(i);
			long base = (long)x.indexAt(i) * (2*n - x.indexAt(i) + 1)/2;
			wAdder(w, base + x.indexAt(i), g*tmp_value*x.valueAt(i));
			for(int j = i + 1; j != x.size(); j++){
			    wAdder(w, base + x.indexAt(j), sqrt2_g*tmp_value*x.valueAt(j));
			}
			wAdder(w, x.indexAt(i), tmp_value*sqrt2cg);
		}

	}

	@Override
	public double snorm(Instance x) {
		// TODO Auto-generated method stub
		double res = g * lk.snorm(x) + c;
		return res * res;
	}

	@Override
	public void add(double[] w, SparseVector x, double factor) {
		wAdder(w, 0, factor * c);
		for(int i = 0; i != x.numLocations(); i++){
			double tmp_value = factor*x.valueAtLocation(i);
			long base = (long)x.indexAtLocation(i) * (2*n - x.indexAtLocation(i) + 1)/2;
			wAdder(w, base + x.indexAtLocation(i), g*tmp_value*x.valueAtLocation(i));
			for(int j = i + 1; j != x.numLocations(); j++){
			    wAdder(w, base + x.indexAtLocation(j), sqrt2_g*tmp_value*x.valueAtLocation(j));
			}
			wAdder(w, x.indexAtLocation(i), tmp_value*sqrt2cg);
		}

		
	}

	@Override
	public double dot(SparseVector x, SparseVector y) {
		double res = g*lk.dot(x, y) + c;
		return res;
	}

	@Override
	public double dot(double[] w, SparseVector x) {
		double sum = this.wGetter(w, 0)*c;	// bias
		for(int i = 0; i != x.numLocations(); i++){
			double tmp_value = 0;
			long base = (long)x.indexAtLocation(i) * (2*n - x.indexAtLocation(i) + 1)/2;
			
			for(int j = i + 1; j != x.numLocations(); j++){
				tmp_value += wGetter(w, base + x.indexAtLocation(j))*x.valueAtLocation(j);
			}
			tmp_value *= sqrt2_g;
			tmp_value += wGetter(w, base + x.indexAtLocation(i))*x.valueAtLocation(i)*g;
			tmp_value += wGetter(w, x.indexAtLocation(i))*sqrt2cg;
			sum += tmp_value*x.valueAtLocation(i);
		}

		return sum;


	}

	@Override
	public double snorm(SparseVector x) {
		double snorm = g * lk.snorm(x) + c;
		return snorm * snorm;
	}
	
	@Override
	public void setwa(wAccessor wa){
		this.wa = wa;
		this.lk.wa = wa;
	}

}
