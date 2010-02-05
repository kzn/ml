package ru.iitp.proling.svm;

import java.util.ArrayList;

import ru.iitp.proling.common.Alphabet;
import gnu.trove.TIntArrayList;

public class VirtualDataset<T> implements Dataset<T> {
	protected Alphabet<T> alphabet;
	protected ArrayList<SparseVector> samples;
	protected TIntArrayList targets;
	protected TIntArrayList qids;
	protected int dim;
	
	public VirtualDataset(Alphabet<T> alphabet){
		this.alphabet = alphabet;
		samples = new ArrayList<SparseVector>();
		targets = new TIntArrayList();
		qids = new TIntArrayList();
		dim = 0;
	}
	
	public int add(SparseVector v, int target, int qid){
		int idx = samples.size();
		samples.add(v);
		targets.add(target);
		qids.add(qid);
		return idx;
	}
	
	public int add(SparseVector v, int target){
		return add(v, target, 0);
	}
	
	public int add(SparseVector v, T target){
		return add(v, alphabet.get(target), 0);
	}

	@Override
	public Alphabet<T> alphabet() {
		return alphabet;
	}

	@Override
	public int max_dim() {
		return dim;
	}

	@Override
	public int qid(int idx) {
		return qids.get(idx);
	}

	@Override
	public int[] qids() {
		return qids.toNativeArray();
	}

	@Override
	public int size() {
		return samples.size();
	}

	@Override
	public int target(int idx) {
		// TODO Auto-generated method stub
		return targets.get(idx);
	}

	@Override
	public int[] targets() {
		return targets.toNativeArray();
	}

	@Override
	public SparseVector vec(int idx) {
		// TODO Auto-generated method stub
		return samples.get(idx);
	}

}
