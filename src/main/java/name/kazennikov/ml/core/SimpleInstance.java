package name.kazennikov.ml.core;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;


public class SimpleInstance implements Instance {
	final int[] index;
	final double[] values;

	
	public SimpleInstance(TIntArrayList index, TDoubleArrayList values) {
		this.index = index.toArray();
		this.values = values.toArray();
	}
	
	public SimpleInstance(int[] index, double[] values) {
		this.index = index;
		this.values = values;
	}

	@Override
	public int indexAt(int index) {
		return this.index[index];
	}

	@Override
	public double valueAt(int index) {
		return this.values[index];
	}

	@Override
	public int size() {
		return index.length;
	}

	@Override
	public int dim() {
		return index[index.length - 1];
	}

}
