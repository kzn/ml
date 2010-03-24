package ru.iitp.proling.svm;

import ru.iitp.proling.common.ArrayUtils;

/**
 * Create cross validation subsets from base dataset
 * @author ant
 *
 * @param <T>
 */
public class SplitDataset<T> {
	public final Dataset<T> train;
	public final Dataset<T> test;
	protected Dataset<T> base;
	
	public SplitDataset(Dataset<T> base, int nfold){
		this.base = base;
		
		DefaultDataset<T> train = new DefaultDataset<T>();
		DefaultDataset<T> test = new DefaultDataset<T>();
		
		int[] indexes = new int[base.size()];
		for(int i = 0; i != indexes.length; i++)
			indexes[i] = i;
		
		ArrayUtils.shuffle(indexes, indexes.length);
		
		int stop = base.size()/nfold;
		
		for(int i = 0; i != base.size(); i++){
			if(i > stop)
				train.add(base.get(i));
			else
				test.add(base.get(i));
		}
		
		this.train = train;
		this.test = test;
		
	}
}
