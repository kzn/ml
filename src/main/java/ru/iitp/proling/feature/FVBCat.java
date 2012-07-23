package ru.iitp.proling.feature;



import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import cc.mallet.types.SparseVector;


public class FVBCat {
	CategorialData cd;
	TIntArrayList indexes = new TIntArrayList();
	TDoubleArrayList values = new TDoubleArrayList();
	
	
	public  FVBCat(CategorialData cd){
		this.cd = cd;
		
	}
	
	
	

	public void add(Object key, Object value) {
		int idx = cd.lookupKey(key);
		int val = cd.lookupValue(idx, value);
		
		indexes.add(idx);
		values.add(val);
	

	}

	public void add(Object key, Object[] values) {
		int idx = cd.lookupKey(key);
		for(Object o : values){
			int val = cd.lookupValue(idx, o);
			
			indexes.add(idx);
			this.values.add(val);
		}
	}


	public void addBinary(Object[] key) {
		int idx = cd.lookupKey(key);
		indexes.add(idx);
		values.add(1.0);
	}

	public SparseVector build() {
		//return new SparseVector(indexes.toNativeArray(), values.toNativeArray(), false);
		return new SparseVector(indexes.toArray(), values.toArray(), false, false, false);
	}

}
