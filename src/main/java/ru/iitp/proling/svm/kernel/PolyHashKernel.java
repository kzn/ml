package ru.iitp.proling.svm.kernel;


import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import name.kazennikov.ml.core.Instance;

import cc.mallet.types.SparseVector;

public class PolyHashKernel extends Kernel implements Serializable {
	protected double g;
	protected double c;
	protected int n;
	protected int D;
	protected LinearKernel lk;

	protected double  sqrt2cg;
	protected double sqrt2_g;

	public PolyHashKernel(double g, double c, int n, int D){
		this.g = g;
		this.c = c;
		this.n = n;
		this.D = D;
		sqrt2cg = Math.sqrt(2*c*g);
		sqrt2_g = Math.sqrt(2)*g;
		lk = new LinearKernel();
		this.wa = new wAccessorHash(D);
		lk.wa = this.wa;
	}

	@Override
	public int dim(int n) {
		return D;//(n+2)*(n+1)/2;
	}

	@Override
	public double dot(Instance x, Instance y) {
		/*TreeMap<Long, Double> p1 = pipe(x);
		TreeMap<Long, Double> p2 = pipe(y);
		
		Set<Entry<Long, Double>> e1 = p1.entrySet();
		Set<Entry<Long, Double>> e2 = p2.entrySet();
		
		Iterator<Entry<Long, Double>> i1 = e1.iterator();
		Iterator<Entry<Long, Double>> i2 = e2.iterator();
		
		while(i1.hasNext() && i2.hasNext()){
			
		}
		
		
		/*double res = g*lk.dot(x, y) + c;
		return res * res;
		*/
		return 0;
	}

	@Override
	public double dot(double[] w, Instance x) {

		//double sum = this.wGetter(w, 0)*c;	// bias
		double sum = wGetter(w, 0) * c;
		for(int i = 0; i != x.size(); i++){
			double tmp_value = 0;
			long base = (long)x.indexAt(i) * (2*n - x.indexAt(i) + 1)/2;

			for(int j = i + 1; j != x.size(); j++){
				//tmp_value += wGetter(w, base + x.indexAt(j))*x.valueAt(j);
				tmp_value += wGetter(w, base + x.indexAt(j)) * x.valueAt(j);
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
		//System.out.println("Hello!");
		//TreeMap<Long, Double> tm = pipe(x);
		//double d = 0;
		//for(Entry<Long, Double> entry : tm.entrySet())
		//	d += entry.getValue() * entry.getValue();
		double res = g * lk.snorm(x) + c;
		return res * res;
		//return d;
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
		//TreeMap<Long, Double> tm = pipe(x);
		double snorm = g * lk.snorm(x) + c;
		return snorm * snorm;
	}

}
