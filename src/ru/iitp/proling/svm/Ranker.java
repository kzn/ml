package ru.iitp.proling.svm;

import java.util.List;
import java.util.SortedMap;

public interface Ranker {
	SortedMap<Double, Instance<?>> rank(List<Instance<?>> lst);
	double score(Instance<?> v);
}
