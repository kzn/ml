package ru.iitp.proling.svm.kernel;

public class wAccessor {
	public void wAdder(double[] dense, long idx, double value) {
		dense[(int) idx] += value;
	}


	public double wGetter(double[] dense, long idx) {
		return dense[(int) idx];
	}


	public void wSetter(double[] dense, long idx, double value) {
		dense[(int)idx] = value;
	}

}
