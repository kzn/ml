package ru.iitp.proling.svm;

import ru.iitp.proling.common.ArrayUtils;

/**
 * Create cross validation subsets from base dataset
 * @author ant
 *
 * @param <T>
 */
public class SplitDataset<T> {
	public final VirtualDataset<T> train;
	public final VirtualDataset<T> test;
	protected Dataset<T> base;
	
	public SplitDataset(Dataset<T> base, int nfold){
		this.base = base;
		
		VirtualDataset<T> train = new VirtualDataset<T>(base.alphabet());
		VirtualDataset<T> test = new VirtualDataset<T>(base.alphabet());
		
		int[] indexes = new int[base.size()];
		for(int i = 0; i != indexes.length; i++)
			indexes[i] = i;
		
		ArrayUtils.shuffle(indexes, indexes.length);
		
		int stop = base.size()/nfold;
		
		for(int i = 0; i != base.size(); i++){
			if(i > stop)
				train.add(base.get(i), base.target(i));
			else
				test.add(base.get(i), base.target(i));
		}
		
		this.train = train;
		this.test = test;
		
	}
}
