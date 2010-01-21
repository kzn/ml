package ru.iitp.proling.svm;

import ru.iitp.proling.svm.kernel.Kernel;

/**
 * One vs. Rest classifier.
 * @author ant
 *
 */

public class OVRClassifier extends BinaryClassifier {
	public OVRClassifier(double[] w, Kernel kernel, int positive){
		super(w, kernel, positive, 0);
	}
	
}

