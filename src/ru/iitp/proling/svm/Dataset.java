package ru.iitp.proling.svm;

import ru.iitp.proling.common.Alphabet;

public interface Dataset {
	/// Return total number of samples in the dataset
	int size();
	
	/// Return greatest dimension found in the dataset
	int max_dim();
	
	/// Return sample idx
	SparseVector vec(int idx);
	
	/// easy metadata access
	int target(int idx);
	int qid(int idx);
	
	int[] targets();
	int[] qids();
	Alphabet alphabet();


}
