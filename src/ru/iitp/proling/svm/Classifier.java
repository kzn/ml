package ru.iitp.proling.svm;
import java.util.*;

import ru.iitp.proling.common.Alphabet;

public abstract class Classifier {
	/// Return actual label index
	abstract int classify(SparseVector v);

	/// Return sorted of distribution of scores for each class 
	abstract SortedMap<Double, Integer> scores();
	
	/// Return label distribution
	abstract Alphabet alphabet();
}
