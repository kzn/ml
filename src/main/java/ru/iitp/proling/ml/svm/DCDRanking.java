package ru.iitp.proling.ml.svm;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;


/**
 * DCD Solver for SVM Ranking problem. <br>
 * 
 * It uses notion of query id - a set of samples that has same query id 
 * is identified as a single rank list
 * @author Anton Kazennikov
 *
 */
public abstract class DCDRanking extends AbstractDCD {

	AbstractDCD base;
	protected double[] targets;
	protected double[] sqnorms;
	
	// actual vector is a - b
	protected int[] a;
	protected int[] b;
	

	/**
	 * Construct DCD Solver for SVM Ranking problem
	 * @param base base problem definition
	 * @param qids query ids for samples in the base problem
	 * @param c_pos C for positive samples
	 * @param c_neg C for negative samples
	 * @param iter max number of iterations to perform
	 * @param eps epsilon optimal stopping critierion
	 * @param threshold threshold of active vars between shuffling and sorting
	 */
	public DCDRanking(AbstractDCD base, int[] qids, 
			double c_pos, double c_neg, int iter, double eps,
			int threshold) {
		super(c_pos, c_neg, iter, eps, threshold);
		
		this.base = base;
		TIntArrayList _a = new TIntArrayList();
		TIntArrayList _b = new TIntArrayList();
		TDoubleArrayList _targets = new TDoubleArrayList();
		
		for(int i = 0; i != qids.length; i++) { // a idx
			for(int j = i; j != qids.length; j++){ // b idx
				int q_i = qids[i];
				int q_j = qids[j];
				
				
				if(q_i != q_j)
					break;
				
				if(base.target(i) == base.target(j))
					continue;
				  
				if(base.target(i) < base.target(j)){ // pair a > b
					_a.add(i);
					_b.add(j);
					_targets.add((base.target(j) - base.target(i))*(1.0));
					
					_a.add(j);
					_b.add(i);
					_targets.add((base.target(i) - base.target(j))*(1.0));
				}else{ // pair b > a
					_a.add(j);
					_b.add(i);
					_targets.add((base.target(i) - base.target(j)) * (1.0));
					
					_a.add(i);
					_b.add(j);
					_targets.add((base.target(j) - base.target(i)) * (1.0));
				}
			}
		}
		a = _a.toArray();
		b = _b.toArray();
		this.targets = _targets.toArray();
		
		
		System.out.printf("Constructed %d ranks\n", a.length);
		
		sqnorms = new double[a.length];
		
		for(int i = 0; i != a.length; i++){
			sqnorms[i] = base.snorm(a[i]) + base.snorm(b[i]);
			sqnorms[i] -= 2 * dot(a[i], b[i]);
		}
	}
	/**
	 * Compute <x1, x2> dot product
	 * @param vec1 index of x1 vector
	 * @param vec2 index of x2 vector
	 */
	public abstract double dot(int vec1, int vec2);

	@Override
	public void init() {
		base.init();
	}

	@Override
	public double snorm(int vec) {
		return sqnorms[vec];
	}

	@Override
	public int size() {
		return a.length;
	}

	@Override
	public double dot(int vec) {
		return base.dot(a[vec]) - base.dot(b[vec]);
	}

	@Override
	public void add(int vec, double factor) {
		base.add(a[vec], factor);
		base.add(b[vec], -factor);
	}

	@Override
	public double target(int vec) {
		return targets[vec];
	}
	
	public AbstractDCD base() {
		return base;
	}
}
