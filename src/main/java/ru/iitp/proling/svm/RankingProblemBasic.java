package ru.iitp.proling.svm;


import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

import name.kazennikov.ml.core.Instance;

import ru.iitp.proling.ml.core.RankingProblem;



public class RankingProblemBasic extends RankingProblem {
	List<List<Instance>> instances = new ArrayList<List<Instance>>();
	List<int[]> ranks = new ArrayList<int[]>();
	
	List<Instance> greater = new ArrayList<Instance>();
	List<Instance> lesser = new ArrayList<Instance>();
	TIntArrayList difference = new TIntArrayList();
	int dim = 0;
	
	public int add(List<Instance> ranking, int[] ranks){
		int index = instances.size();
		instances.add(ranking);
		this.ranks.add(ranks);

		// add pairs
		for(int i = 0; i != ranking.size(); i++){ // a idx
			for(int j = i; j != ranking.size(); j++){ // b idx
				
				if(ranks[i] == ranks[j])
					continue;
				  
				if(ranks[i] < ranks[j]){ // pair a > b
					greater.add(ranking.get(i));
					lesser.add(ranking.get(j));
					difference.add(ranks[j] - ranks[i]);
				}else{
					greater.add(ranking.get(j));
					lesser.add(ranking.get(i));
					difference.add(ranks[i] - ranks[j]);
				}
			}
			dim = Math.max(dim, ranking.get(i).dim());
		}
		return index;
	}
	

	@Override
	public int dim() {
		return dim;
	}

	@Override
	public List<Instance> get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instance pairGreater(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instance pairLesser(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int pairSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int rankDifference(int index) {

		return 0;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] target(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
