package name.kazennikov.ml.sgd;

import java.util.Arrays;

import name.kazennikov.ml.core.Instance;


public class DenseVector {
	public static void combine(double[] v1, double c1, double[] v2, double c2) {
		for(int i = 0; i < v1.length; i++) {
			v1[i] = v1[i] * c1 + v2[i] * c2;
		}
	}
	
	public static void scale(double[] v, double factor) {
		for(int i = 0; i < v.length; i++) {
			v[i] *= factor;
		}
	}
	
	public static double dot(double[] v1, double v2[]) {
		double d = 0;
		int dim = Math.min(v1.length, v2.length);
		for(int i = 0; i < dim; i++) {
			d += v1[i] * v2[i];
		}
		
		return d;
	}
	
	public static double dot(double[] v, Instance x) {
		double d = 0;
		
		for(int i = 0; i < x.size(); i++) {
			d += v[x.indexAt(i)] * x.valueAt(i);
		}
		
		return d;
	}
	
	public static void add(double[] v, Instance x, double factor) {
		for(int i = 0; i < x.size(); i++) {
			v[x.indexAt(i)] += x.valueAt(i) * factor;
		}
	}
	
	public static void clear(double[] v) {
		Arrays.fill(v, 0.0);
	}



}
