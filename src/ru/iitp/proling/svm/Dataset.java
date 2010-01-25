package ru.iitp.proling.svm;

import ru.iitp.proling.common.Alphabet;
/**
 * Dataset interface. Dataset is a set of samples, represented by vector and it's labeling.
 * Additionally, dataset provides a qid metadata - query id of the sample, if any. This is used
 * in ranking applications
 * @author ant
 *
 */
public interface Dataset {
	/**
	 * Get number of samples in the dataset
	 * @return Number of samples in the dataset
	 */
	int size();
	
	/**
	 * Find dimension of dataset. Find the maximal used vector dimension of samples
	 * @return Maximal dataset dimension
	 */
	int max_dim();
	
	/**
	 * Get sample from the dataset. Returns a reference, not a fresh object 
	 * @param idx index of the sample
	 * @return SparseVector representing the sample. 
	 */
	SparseVector vec(int idx);
	
	/**
	 * Return target index of label of sample \a idx, as mapped by internal alphabet
	 * @param idx sample index
	 * @return index of the target
	 */
	int target(int idx);
	/**
	 * Return query id of sample \a idx
	 * @param idx sample index
	 * @return query id, 0 if none
	 */
	int qid(int idx);
	
	/**
	 * Return array of targets of all samples.
	 * @return array of size size() of targets of all samples
	 */
	int[] targets();
	
	/**
	 * Return array of query ids of all samples
	 * @return array of size size() of query ids of all samples
	 */
	int[] qids();
	
	/**
	 * Get the alphabet used.
	 * @return Alphabet, used to map input label to int
	 */
	Alphabet alphabet();


}
