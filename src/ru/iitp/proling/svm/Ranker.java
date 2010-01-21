package ru.iitp.proling.svm;

import java.util.List;
import java.util.SortedMap;

public interface Ranker {
	SortedMap<Double, SparseVector> rank(List<SparseVector> lst);
	double score(SparseVector v);
}
