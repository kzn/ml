package ru.iitp.proling.svm;

public class ClassifierEval {
	
	public static double evalBinaryClassifier(Dataset dataset, BinaryClassifier classifier){
		int misclassified = 0;
		
		
		
		for(int i = 0; i != dataset.size(); i++){
			if(classifier.classify(dataset.vec(i)) != dataset.target(i))
				misclassified++;
		}
		
		double accuracy = 1.0*misclassified/dataset.size();
		
		System.out.println("Classifier accuracy:" + accuracy);
		System.out.printf("Accuracy: %d/%d\n ", misclassified, dataset.size());
		
		return accuracy; 
	}
	
		

}
