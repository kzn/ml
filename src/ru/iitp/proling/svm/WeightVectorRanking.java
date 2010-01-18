package ru.iitp.proling.svm;
import java.util.ArrayList;
import java.util.List;


public class WeightVectorRanking extends WeightVector {
	protected WeightVector w;
	protected double[] alphas;
	protected double[] targets;
	protected double[] sqnorms;
	
	// actual vector is a - b
	protected int[] a;
	protected int[] b;
	
	
	
	public WeightVectorRanking(WeightVector base, List<Integer> qids){
		w = base;
		List<Integer> _a = new ArrayList<Integer>();
		List<Integer> _b = new ArrayList<Integer>();
		List<Double> _targets = new ArrayList<Double>();
		
		
		
		
		for(int i = 0; i != qids.size(); i++){ // a idx
			for(int j = i; j != qids.size(); j++){ // b idx
				int q_i = qids.get(i);
				int q_j = qids.get(j);
				
				
				if(q_i != q_j)
					break;
				
				if(w.target(i) == w.target(j))
					continue;
				  
				if(w.target(i) > w.target(j)){ // pair a > b
					_a.add(i);
					_b.add(j);
					_targets.add(1.0);
				}else{ // pair b > a
					_a.add(j);
					_b.add(i);
					_targets.add(1.0);
				}
			}
		}
		a = new int[_a.size()];
		b = new int[_b.size()];
		targets = new double[_targets.size()];
		
		for(int i = 0; i != _a.size(); i++){
			a[i] = _a.get(i);
			b[i] = _b.get(i);
			targets[i] = _targets.get(i);
			
		}
		
		System.out.printf("Constructed %d ranks\n", a.length);
		
		sqnorms = new double[a.length];
		
		for(int i = 0; i != a.length; i++){
			sqnorms[i] = w.snorm(a[i]) + w.snorm(b[i]);
			sqnorms[i] -= 2 * w.dot(a[i], b[i]);
		}
				
		alphas = new double[a.length];
	}

	@Override
	public void add(int idx, double factor) {
		w.add(a[idx], factor);
		w.add(b[idx], -factor);
	}

	@Override
	public void add_alpha(int idx, double factor) {
		alphas[idx] += factor;
	}

	@Override
	public double alpha(int idx) {
		return alphas[idx];
	}

	@Override
	public int dim() {
		return w.dim();
	}

	@Override
	public double dot(int idx) {
		return w.dot(a[idx]) - w.dot(b[idx]);
	}
	
	@Override
	public double dot(int x, int y){
		int _a = a[x];
		int _b = b[x];
		int _c = a[y];
		int _d = b[y];
		return w.dot(_a, _c) - w.dot(_a, _d) - w.dot(_b, _c) + w.dot(_b, _d);
	}

	@Override
	public double norm() {
		return w.norm();
	}

	@Override
	public int size() {
		return a.length;
	}

	@Override
	public double snorm() {
		return w.snorm();
	}

	@Override
	public double snorm(int idx) {
		return sqnorms[idx];
	}

	@Override
	public double target(int idx) {
		return targets[idx];
	}

}
