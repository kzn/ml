package ru.iitp.proling.svm;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import ru.iitp.proling.common.Alphabet;

public class DefaultDataset<T> implements Dataset<T> {
	protected ArrayList<Instance<T>> samples = new ArrayList<Instance<T>>();
	protected Alphabet<T> alphabet = new Alphabet<T>();
	protected int dim = 0;
	
	public int add(Instance<T> v){
		int idx = samples.size();
		samples.add(v);
		dim = Math.max(dim, v.dim());
		alphabet.get(v.value());
		return idx;
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
		TreeSet<T> ts = new TreeSet<T>();
		ts.addAll(alphabet.entries());
		return ts;
	}

}
