package ru.iitp.proling.svm;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.iitp.proling.ml.core.WeightVector;
/**
 * Ranking problem formulation for SVM classifier
 * @author ant
 * Assumes, that dataset is divided into ranks - sets of examples weighted by
 * integer value of example target.
 * 
 *  For example, rank of:
 *  1 v1
 *  2 v2
 *  1 v3
 *  There v2 sample is ranked higher than v1 and v3
 *  
 *  Learning is based on pairwise strategy. 
 *
 */

public class WeightVectorRanking extends WeightVectorProxy {
	protected double[] alphas;
	protected double[] targets;
	protected double[] sqnorms;
	
	// actual vector is a - b
	protected int[] a;
	protected int[] b;
	
	
	
	public WeightVectorRanking(WeightVector base, List<Integer> qids){
		super(base);
		TIntArrayList _a = new TIntArrayList();
		TIntArrayList _b = new TIntArrayList();
		TDoubleArrayList _targets = new TDoubleArrayList();
		
		
		
		
		for(int i = 0; i != qids.size(); i++){ // a idx
			for(int j = i; j != qids.size(); j++){ // b idx
				int q_i = qids.get(i);
				int q_j = qids.get(j);
				
				
				if(q_i != q_j)
					break;
				
				if(w.target(i) == w.target(j))
					continue;
				  
				if(w.target(i) < w.target(j)){ // pair a > b
					_a.add(i);
					_b.add(j);
					_targets.add((w.target(j) - w.target(i))*(1.0 + w.cost(i) - w.cost(j)));
					
					_a.add(j);
					_b.add(i);
					_targets.add((w.target(i) - w.target(j))*(1.0 + w.cost(i) - w.cost(j)));
				}else{ // pair b > a
					_a.add(j);
					_b.add(i);
					_targets.add((w.target(i) - w.target(j)) * (1.0 + w.cost(i) - w.cost(j)));
					
					_a.add(i);
					_b.add(j);
					_targets.add((w.target(j) - w.target(i)) * (1.0 + w.cost(i) - w.cost(j)));

				}
			}
		}
		a = _a.toArray();
		b = _b.toArray();
		targets = _targets.toArray();
		
		
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
	public int size() {
		return a.length;
	}

	@Override
	public double snorm(int idx) {
		return sqnorms[idx];
	}

	@Override
	public double target(int idx) {
		return targets[idx];
	}
	
	@Override
	public void clear(){
		w.clear();
		Arrays.fill(alphas, 0.0);
	}
	
	// pair accessors. a[idx] must be ranked higher, than b[idx]
	public int a(int idx){
		return a[idx];
	}
	
	public int b(int idx){
		return b[idx];
	}

}
