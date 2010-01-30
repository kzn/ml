package ru.iitp.proling.svm;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.iitp.proling.svm.kernel.Kernel;

public class SimpleRanker implements Ranker {
	protected Kernel k;
	protected Scorer scorer;
	
	public SimpleRanker(Scorer scorer){
		this.scorer = scorer;
	}

	@Override
	public SortedMap<Double, SparseVector> rank(List<SparseVector> lst) {
		SortedMap<Double, SparseVector> sm = new TreeMap<Double, SparseVector>();
		for(SparseVector v : lst)
			sm.put(score(v), v);
		
		
		return sm;
	}

	@Override
	public double score(SparseVector x) {
		return scorer.score(x);
	}

}
