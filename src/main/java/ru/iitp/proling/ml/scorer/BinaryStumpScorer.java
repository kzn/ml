package ru.iitp.proling.ml.scorer;

import name.kazennikov.ml.core.Instance;
import cc.mallet.types.SparseVector;

public class BinaryStumpScorer extends Scorer {
	final int index;
	public BinaryStumpScorer(int index){
		this.index = index;
	}

	@Override
	public double score(Instance v) {
		for(int i = 0; i != v.size(); i++)
			if(v.indexAt(i) == index)
				return 1.0;
		return -1.0;
	}

	@Override
	public double score(SparseVector v) {
		for(int i = 0; i != v.numLocations(); i++)
			if(v.indexAtLocation(i) == index)
				return 1.0;
		return -1.0;
	}

}
