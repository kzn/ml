package ru.iitp.proling.svm;

import gnu.trove.TIntArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class ClassifierEval {
	
	public static double evalBinaryClassifier(Dataset dataset, BinaryClassifier classifier){
		int misclassified = 0;
		
		
		
		for(int i = 0; i != dataset.size(); i++){
			if(classifier.classify(dataset.get(i)) != dataset.target(i))
				misclassified++;
		}
		
		double accuracy = 1.0*misclassified/dataset.size();
		
		System.out.println("Classifier accuracy:" + accuracy);
		System.out.printf("Accuracy: %d/%d\n ", misclassified, dataset.size());
		
		return accuracy; 
	}
	
	public static double evalOVRClassifier(Dataset dataset, OVRClassifier classifier){
		int misclassified = 0;
		
		
		
		for(int i = 0; i != dataset.size(); i++){
			int cls = classifier.classify(dataset.get(i));
			if(cls != -1){
				if(cls == dataset.target(i))
					misclassified++;
			}else{
				if(dataset.target(i) == classifier.positive)
					misclassified++;
			}
				
		}
		
		double accuracy = 1.0*misclassified/dataset.size();
		
		System.out.println("Classifier accuracy:" + accuracy);
		System.out.printf("Accuracy: %d/%d\n ", misclassified, dataset.size());
		
		return accuracy;
		
	}
	
	
	public static double evalRanker(BasicDataset dtest, SimpleRanker sr){
		HashMap<Integer, TIntArrayList> sample_lists = new HashMap<Integer, TIntArrayList>();
		
		for(int i = 0; i != dtest.size(); i++){
			int query_id = dtest.qid(i);
			if(!sample_lists.containsKey(query_id))
				sample_lists.put(query_id, new TIntArrayList());
			
			sample_lists.get(query_id).add(i);
		}
		
		int swapped = 0;
		int correct = 0;
		
		// for each query id
		//for(int i = 0; i != sample_lists.size(); i++){
		for(Entry<Integer, TIntArrayList> entry : sample_lists.entrySet()){
			List<SparseVector> vecs = new ArrayList<SparseVector>();
			double[] ref = new double[entry.getValue().size()];
			for(int j = 0; j != entry.getValue().size(); j++){
				int idx = entry.getValue().get(j);
				vecs.add(dtest.get(idx));
				ref[j] = dtest.alphabet().get(dtest.target(idx));
			}
			
			double[] predicted = sr.score(vecs);
			int this_swapped = sr.swappedPairs(ref, predicted);
			if(this_swapped == 0)
				correct++;
			swapped += this_swapped;
		}
		
		System.out.println("Swapped pairs on test set:" + swapped);
		System.out.printf("Correct rankings:%d/%d\n", correct, sample_lists.size());
		return 1.0*correct/sample_lists.size();
	}

		

	
		

}
