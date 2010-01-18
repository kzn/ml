package ru.iitp.proling.svm;
import gnu.trove.*;


public class WeightVectorSlice extends WeightVectorProxy {
	//protected List<Integer> slice;
	protected TIntArrayList slice;
		
	public WeightVectorSlice(WeightVector base){
		super(base);
		//slice = new ArrayList<Integer>();
		slice = new TIntArrayList();
	}
	
	public void add_vec(int idx){
		slice.add(idx);
	}
	
	@Override
	public void add(int idx, double factor) {
		w.add(slice.get(idx), factor);
	}

	@Override
	public void add_alpha(int idx, double factor) {
		w.add_alpha(slice.get(idx), factor);
	}
	

	@Override
	public double alpha(int idx) {
		return w.alpha(slice.get(idx));
	}

	@Override
	public double dot(int idx) {
		return w.dot(slice.get(idx));
	}

	@Override
	public double dot(int x, int y) {
		return w.dot(slice.get(x),slice.get(y));
	}

	@Override
	public double snorm(int idx) {
		return w.snorm(slice.get(idx));
	}

	@Override
	public double target(int idx) {
		return w.target(slice.get(idx));
	}
	
	@Override
	public int size(){
		return slice.size();
	}

}
