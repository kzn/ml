package ru.iitp.proling.svm;

import ru.iitp.proling.svm.kernel.Kernel;

/**
 * One vs. Rest classifier. Used as a detector - i.e. on positive classification returns class. 
 * On negative classification returns -1(default value to negative evidence)
 * @author ant
 *
 */

public class OVRClassifier extends BinaryClassifier {
	public OVRClassifier(double[] w, Kernel kernel, int positive){
		super(w, kernel, positive, -1);
	}
	
}

