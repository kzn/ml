package ru.iitp.proling.svm.kernel;

import java.io.Serializable;

public class wAccessor implements Serializable{
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
	
	public int mapCoordinate(long idx){
		return (int)idx;
	}

}
