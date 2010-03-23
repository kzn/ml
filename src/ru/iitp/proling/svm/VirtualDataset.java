package ru.iitp.proling.svm;

import java.util.ArrayList;
import java.util.SortedSet;

import ru.iitp.proling.common.Alphabet;
import gnu.trove.TIntArrayList;

public class VirtualDataset<T> implements Dataset<T> {
	protected ArrayList<Instance<T>> samples;
	protected TIntArrayList targets;
	protected TIntArrayList qids;
	protected int dim;
	
	public VirtualDataset(){
		samples = new ArrayList<Instance<T>>();
		targets = new TIntArrayList();
		qids = new TIntArrayList();
		dim = 0;
	}
	
	public int add(Instance<T> v, int target, int qid){
		int idx = samples.size();
		samples.add(v);
		targets.add(target);
		qids.add(qid);
		return idx;
	}
	
	public int add(Instance<T> v, int target){
		return add(v, target, 0);
	}
	
	public int add(Instance<T> v, T target){
		// FIXME target is not 0
		return add(v, 0, 0);
	}


	@Override
	public int dim() {
		return dim;
	}

	@Override
	public int size() {
		return samples.size();
	}


	@Override
	public Instance<T> get(int idx) {
		return samples.get(idx);
	}

	@Override
	public SortedSet<T> classes() {
		return null;
	}

}
