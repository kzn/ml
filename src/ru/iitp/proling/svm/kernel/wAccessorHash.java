package ru.iitp.proling.svm.kernel;

public class wAccessorHash extends wAccessor {
	protected int n;
	
	public wAccessorHash(int n) {
		this.n = n;
	
	}
	
	public int hash(long idx){
		Long k = idx;
		return k.hashCode() % n; 
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

}
