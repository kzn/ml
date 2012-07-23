package ru.iitp.proling.svm;

import cc.mallet.types.SparseVector;
import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.scorer.Scorer;

public class TrivialBinaryClassifier {
	final Scorer scorer;
	public TrivialBinaryClassifier(Scorer s){
		this.scorer = s;
	}
	
	public double classify(Instance x){
		return scorer.score(x) > 0? 1 : -1;
	}
	
	public double classify(SparseVector x){
		return scorer.score(x) > 0? 1 : -1;
	}
	
	

}
