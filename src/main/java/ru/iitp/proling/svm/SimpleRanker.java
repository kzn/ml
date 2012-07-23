package ru.iitp.proling.svm;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.Ranker;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.kernel.Kernel;

public class SimpleRanker implements Ranker {
	protected Kernel k;
	protected Scorer scorer;
	
	public SimpleRanker(Scorer scorer){
		this.scorer = scorer;
	}

	@Override
	public SortedMap<Double, Instance> rank(List<Instance> lst) {
		SortedMap<Double, Instance> sm = new TreeMap<Double, Instance>();
		for(Instance v : lst)
			sm.put(score(v), v);
		
		
		return sm;
	}

	@Override
	public double score(Instance x) {
		return scorer.score(x);
	}
	
	public double[] score(List<Instance> vec){
		double[] res = new double[vec.size()];
		int i = 0;
		
		for(Instance v : vec)
			res[i++] = score(v);
		
		return res;
			
	}
	
	
	public int swappedPairs(double[] correct, double[] predicted){
		int n = 0;
		for(int i = 0; i != correct.length; i++){
			for(int j = 0; j != correct.length; j++){
				if(correct[i] > correct[j] && predicted[i] <= predicted[j])
					n++;
			}
		}
		return n;
	}
	
	public double avgSwappedPairs(double[] correct, double[] predicted){
		int n = 0;
		int total = 0;
		
		for(int i = 0; i != correct.length; i++){
			for(int j = 0; j != correct.length; j++){
				if(correct[i] > correct[j]){
					total++;
					if(predicted[i] <= predicted[j])
						n++;
				}
			}
		}
		
		return 1.0*n/total;
	}

}
