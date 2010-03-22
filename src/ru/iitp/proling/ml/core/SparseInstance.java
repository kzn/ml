package ru.iitp.proling.ml.core;

import java.util.Iterator;
import java.util.List;

public class SparseInstance<T> implements Instance<T> {
	int[] indexes;
	double[] values;
	T value;
	
	public SparseInstance(T value, List<Integer> indexes, List<Double> values){
		assert(indexes.size() == values.size());
		this.indexes = new int[indexes.size()];
		this.values = new double[values.size()];
			
		for(int i = 0; i != indexes.size(); i++){
			this.indexes[i] = indexes.get(i);
			this.values[i] = values.get(i);
		}
	}

	@Override
	public double get(int idx) {
		return 0;
	}

	@Override
	public int[] indexes() {
		return indexes;
	}
	
	@Override
	public double[] values() {
		return values;
	}


	@Override
	public T value() {
		return value;
	}

	@Override
	public Iterator<Feature> iterator() {
		
		return new FeatIterator();
	}
	
	class FeatIterator implements Iterator<Feature>{
		int idx = 0;
		int size = 0;
		
		public FeatIterator(){
			this.size = indexes.length;
			
		}
		

		@Override
		public boolean hasNext() {
			return idx != size;
		}

		@Override
		public Feature next() {
			int curr = idx++;
			return new Feature(indexes[curr], values[curr]);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove is not supported by Feature Iterator");
		}
		
	}


	
}
