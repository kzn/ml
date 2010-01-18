package ru.iitp.proling.svm;

public interface Dataset {
	/// Return total number of samples in the dataset
	int size();
	
	/// Return squared norm of vector idx
	double snorm(int idx);
	
	/// Return greatest dimension found in the dataset
	int max_dim();
	
	/// Return sample idx
	RWSample vec(int idx);


}
