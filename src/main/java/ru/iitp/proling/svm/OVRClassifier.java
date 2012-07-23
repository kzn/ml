package ru.iitp.proling.svm;

import ru.iitp.proling.ml.scorer.Scorer;

/**
 * One vs. Rest classifier. Used as a detector - i.e. on positive classification returns class. 
 * On negative classification returns -1(default value to negative evidence)
 * @author ant
 *
 */

public class OVRClassifier<T> extends BinaryClassifier<T> {
	public OVRClassifier(Scorer scorer, T positive){
		super(scorer, positive, null);
	}
	
}

