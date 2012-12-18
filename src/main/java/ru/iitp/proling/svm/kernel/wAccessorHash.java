package ru.iitp.proling.svm.kernel;

import java.io.Serializable;

import name.kazennikov.common.MurmurHash;

public class wAccessorHash extends wAccessor implements Serializable{
	protected int n;
	
	public wAccessorHash(int n) {
		this.n = n;
	
	}
	
	public int hash(long idx){
		int h = Math.abs(MurmurHash.hash(idx, 1));
		//int h = Math.abs(Long.valueOf(idx).hashCode());
		//h = h % n;
		//long h = idx % n;
		return h % n;
	}
	
	@Override
	public void wAdder(double[] dense, long idx, double value) {
		dense[hash(idx)] += value;
	}

	@Override
	public double wGetter(double[] dense, long idx) {
		return dense[hash(idx)];
	}

	@Override
	public void wSetter(double[] dense, long idx, double value) {
		dense[hash(idx)] = value;
	}
	
	public int mapCoordinate(long idx){
		return hash(idx);
	}


}
