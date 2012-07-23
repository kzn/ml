package ru.iitp.proling.ml.core;

import cc.mallet.types.SparseVector;

public class MalletInstance implements Instance {
	
	final SparseVector sv;
	
	public MalletInstance(SparseVector sv){
		this.sv = sv;
	}

	@Override
	public int indexAt(int index) {
		return sv.indexAtLocation(index);
	}

	@Override
	public int size() {
		return sv.numLocations();
	}

	@Override
	public double valueAt(int index) {
		return sv.valueAtLocation(index);
	}
	
	@Override
	public int dim(){
		return sv.singleSize();
	}

}
