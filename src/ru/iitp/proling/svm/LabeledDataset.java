package ru.iitp.proling.svm;

import java.util.SortedSet;

/**
 * Dataset interface. Dataset is a set of samples, represented by vector and it's labeling.
 * Additionally, dataset provides a qid metadata - query id of the sample, if any. This is used
 * in ranking applications
 * @author ant
 *
 */
public interface LabeledDataset<T> {
	/**
	 * Get number of samples in the dataset
	 * @return Number of samples in the dataset
	 */
	public int size();
	
	/**
	 * Find dimension of dataset. Find the maximal used vector dimension of samples
	 * @return Maximal dataset dimension
	 */
	public int dim();
	
	/**
	 * Get sample from the dataset. Returns a reference, not a fresh object 
	 * @param idx index of the sample
	 * @return SparseVector representing the sample. 
	 */
	public Instance<T> get(int idx);
	
	/**
	 * Return all classes found in dataset
	 * @return
	 */
	public SortedSet<T> classes();
	

}
