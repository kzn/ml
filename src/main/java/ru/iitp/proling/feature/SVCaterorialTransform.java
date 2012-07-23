package ru.iitp.proling.feature;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.Serializable;
import java.util.Arrays;

import ru.iitp.proling.common.MurmurHash;

import cc.mallet.types.Alphabet;
import cc.mallet.types.SparseVector;

public class SVCaterorialTransform implements Serializable{
	
	static class Pair implements Serializable{

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + key;
			result = prime * result + Arrays.hashCode(value);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (key != other.key)
				return false;
			if (!Arrays.equals(value, other.value))
				return false;
			return true;
		}

		public final int key;
		public final double[] value;
		
		public Pair(int key, double[] value){
			this.key = key;
			this.value = value;
		}
		
	
		
	}

	TObjectIntHashMap<Object> alphabet = new TObjectIntHashMap<Object>();
	int nFeats;
	
	public SVCaterorialTransform(int nFeats){
		this.nFeats = nFeats;
	}
	
	public SparseVector map(SparseVector sv){
		TIntArrayList v = new TIntArrayList(sv.numLocations());
		//double dist = sv.value(14);
		//double domPos = sv.value(1);
		//double depPos = sv.value(2);
		
		
		for(int i = 0; i != sv.numLocations(); i++){
			//v.add((MurmurHash.hash(sv.valueAtLocation(i), sv.indexAtLocation(i)) % nFeats) + 1);

			addPair(v, new Pair(sv.indexAtLocation(i), new double[]{sv.valueAtLocation(i)}));
			/*addPair(v, new Pair(sv.indexAtLocation(i), new double[]{sv.valueAtLocation(i), dist}));
			addPair(v, new Pair(sv.indexAtLocation(i), new double[]{sv.valueAtLocation(i), depPos}));
			addPair(v, new Pair(sv.indexAtLocation(i), new double[]{sv.valueAtLocation(i), domPos}));*/
			/*addPair(v, new Pair(sv.indexAtLocation(i), new double[]{sv.valueAtLocation(i), dist, depPos}));
			addPair(v, new Pair(sv.indexAtLocation(i), new double[]{sv.valueAtLocation(i), dist, domPos}));
			addPair(v, new Pair(sv.indexAtLocation(i), new double[]{sv.valueAtLocation(i), dist, domPos, depPos}));*/

		}
		return new SparseVector(v.toArray(), false);
		
	}
	
	void addPair(TIntArrayList vec, final Pair s){
		int key = alphabet.get(s);
		if(key == 0){
			key = alphabet.size() + 1;
			alphabet.put(s, key);
		}
		vec.add(key);
	}
	
	public int size(){
		return alphabet.size();
	}

}
