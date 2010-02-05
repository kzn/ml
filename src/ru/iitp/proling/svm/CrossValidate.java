package ru.iitp.proling.svm;

import ru.iitp.proling.common.ArrayUtils;

public class CrossValidate<T> {
	public VirtualDataset<T> train;
	public VirtualDataset<T> test;
	protected Dataset<T> base;
	
	public CrossValidate(Dataset<T> base, int nfold){
		this.base = base;
		
		train = new VirtualDataset<T>(base.alphabet());
		test = new VirtualDataset<T>(base.alphabet());
		
		int[] indexes = new int[base.size()];
		for(int i = 0; i != indexes.length; i++)
			indexes[i] = i;
		
		ArrayUtils.shuffle(indexes, indexes.length);
		
		int stop = base.size()/nfold;
		
		for(int i = 0; i != base.size(); i++){
			if(i > stop)
				train.add(base.vec(i), base.target(i));
			else
				test.add(base.vec(i), base.target(i));
		}
		
	}
}
