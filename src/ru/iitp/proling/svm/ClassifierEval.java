package ru.iitp.proling.svm;

import gnu.trove.TIntArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ru.iitp.proling.ml.core.Dataset;
import ru.iitp.proling.ml.core.Instance;

public class ClassifierEval {
	
	public static <T> double evalBinaryClassifier(Dataset<T> dataset, BinaryClassifier<T> classifier){
		int misclassified = 0;
		
		
		
		for(int i = 0; i != dataset.size(); i++){
			if(classifier.classify(dataset.get(i)) != dataset.get(i).value())
				misclassified++;
		}
		
		double accuracy = 1.0*misclassified/dataset.size();
		
		System.out.println("Classifier accuracy:" + accuracy);
		System.out.printf("Accuracy: %d/%d\n ", misclassified, dataset.size());
		
		return accuracy; 
	}
	
	public static <T> double evalOVRClassifier(Dataset<T> dataset, OVRClassifier<T> classifier){
		int misclassified = 0;
		
		
		
		for(int i = 0; i != dataset.size(); i++){
			T cls = classifier.classify(dataset.get(i));
			if(cls != null){
				if(cls == dataset.get(i).value())
					misclassified++;
			}else{
				if(dataset.get(i).value() == classifier.positive)
					misclassified++;
			}
				
		}
		
		double accuracy = 1.0*misclassified/dataset.size();
		
		System.out.println("Classifier accuracy:" + accuracy);
		System.out.printf("Accuracy: %d/%d\n ", misclassified, dataset.size());
		
		return accuracy;
		
	}
	
	
	public static double evalRanker(DatasetList<Double> dtest, SimpleRanker sr){
		int swapped = 0;
		int correct = 0;
		
		// for each query
		for(int k = 0; k != dtest.size(); k++){
			List<Instance<Double>> entry = dtest.get(k);
			List<Instance<Double>> vecs = new ArrayList<Instance<Double>>();
			
			double[] ref = new double[entry.size()];
			for(int j = 0; j != entry.size(); j++){
				//int idx = entry.get(j);
				vecs.add(entry.get(j));
				ref[j] = entry.get(j).value();
			}
			
			double[] predicted = new double[vecs.size()];
			int i = 0;
			
			for(Instance<?> v : vecs){
				predicted[i++] = sr.score(v);
			}
			
			int this_swapped = sr.swappedPairs(ref, predicted);
			if(this_swapped == 0)
				correct++;
			swapped += this_swapped;
		}
		
		System.out.println("Swapped pairs on test set:" + swapped);
		System.out.printf("Correct rankings:%d/%d\n", correct, dtest.size());
		return 1.0*correct/dtest.size();
	}

		

	
		

}
