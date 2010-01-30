package ru.iitp.proling.svm;

/**
 * One vs. Rest classifier. Used as a detector - i.e. on positive classification returns class. 
 * On negative classification returns -1(default value to negative evidence)
 * @author ant
 *
 */

public class OVRClassifier extends BinaryClassifier {
	public OVRClassifier(Scorer scorer, int positive){
		super(scorer, positive, -1);
	}
	
}

