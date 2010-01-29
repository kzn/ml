package ru.iitp.proling.svm.kernel;

public class wAccessor {
	public void wAdder(double[] dense, long idx, double value) {
		dense[(int) idx] += value;
	}


	public double wGetter(double[] dense, long idx) {
		try{
			return dense[(int) idx];
		}
		catch(ArrayIndexOutOfBoundsException e){
			return 0;
		}
	}


	public void wSetter(double[] dense, long idx, double value) {
		dense[(int)idx] = value;
	}

}
