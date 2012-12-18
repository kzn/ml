package ru.iitp.proling.svm;

import java.util.ArrayList;
import name.kazennikov.common.Alphabet;
import name.kazennikov.ml.core.NativeInstance;
import ru.iitp.proling.ml.core.Dataset;

public class DefaultDataset<T> implements Dataset<T> {
	protected ArrayList<NativeInstance<T>> samples = new ArrayList<NativeInstance<T>>();
	protected Alphabet<T> alphabet = new Alphabet<T>();
	protected int dim = 0;
	
	public int add(NativeInstance<T> v){
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
	public NativeInstance<T> get(int idx) {
		return samples.get(idx);
	}

	@Override
	public Alphabet<T> alphabet() {
		return alphabet;
	}


}
