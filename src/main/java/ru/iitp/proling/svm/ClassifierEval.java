package ru.iitp.proling.svm;




import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ru.iitp.proling.ml.core.Dataset;
import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.NativeInstance;

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
	
	
	
	public static double evalRanker(BasicDataset dtest, SimpleRanker sr){
		HashMap<Integer, TIntArrayList> sample_lists = new HashMap<Integer, TIntArrayList>();
		
		for(int i = 0; i != dtest.size(); i++){
			int query_id = dtest.get(i).qid;
			if(!sample_lists.containsKey(query_id))
				sample_lists.put(query_id, new TIntArrayList());
			
			sample_lists.get(query_id).add(i);
		}
		
		int swapped = 0;
		int correct = 0;
		
		// for each query id
		//for(int i = 0; i != sample_lists.size(); i++){
		
		for(Entry<Integer, TIntArrayList> entry : sample_lists.entrySet()){
			List<Instance> vecs = new ArrayList<Instance>();
			
			double[] ref = new double[entry.getValue().size()];
			for(int j = 0; j != entry.getValue().size(); j++){
				int idx = entry.getValue().get(j);
				vecs.add(dtest.get(idx));
				ref[j] = dtest.alphabet().get(dtest.get(idx).value());
			}
			if(ref.length > 0){
				int same = 0;
				for(double r : ref)
					if(r == ref[0])
						same++;
				if(same == ref.length){
					if(ref[0] == 1)
						correct++;
					continue;
				}
					
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
	
	
	public static double evalRanker(DatasetList<Double> dtest, SimpleRanker sr){
		int swapped = 0;
		int correct = 0;
		
		// for each query
		for(int k = 0; k != dtest.size(); k++){
			List<NativeInstance<Double>> entry = dtest.get(k);
			List<NativeInstance<Double>> vecs = new ArrayList<NativeInstance<Double>>();
			
			double[] ref = new double[entry.size()];
			for(int j = 0; j != entry.size(); j++){
				//int idx = entry.get(j);
				vecs.add(entry.get(j));
				ref[j] = entry.get(j).value();
			}
			
			double[] predicted = new double[vecs.size()];
			int i = 0;
			
			for(NativeInstance<?> v : vecs){
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
	
	public static double evalBinaryClassifierMallet(List<Instance> dataset, double[] targets, BinaryClassifier<Double> classifier){
		int misclassified = 0;
		int tp = 0;
		int tn = 0;
		int fp = 0;
		int fn = 0;
		int totPositive = 0;
		
		
		
		for(int i = 0; i != dataset.size(); i++){
			if(classifier.classify(dataset.get(i)) != targets[i]){
				misclassified++;
			
				if(targets[i] == 1.0)
					fn++;
				else
					fp++;
			}
				
			else{
				if(targets[i] == 1.0)
					tp++;
				else
					tn++;
			}
			
			if(targets[i] == 1.0)
				totPositive++;
			
		}
		
		double errRate = 1.0*misclassified/dataset.size();
		double precision = 1.0*tp/(tp + fp);
		double recall = 1.0*tp/(tp + fn);
		
		double f = 2* precision * recall /(precision + recall);
		
		System.out.printf("Total positives/negatives: %d/%d%n", totPositive, dataset.size() - totPositive);
		System.out.printf("Classifier error:%f%n",errRate);
		System.out.printf("Error: %d/%d%n", misclassified, dataset.size());
		System.out.printf("True positives/negatives: %d/%d%n", tp, tn);
		System.out.printf("False positives/negatives: %d/%d%n", fp, fn);
		System.out.printf("Precision/Recall:%f/%f%n", precision, recall);
		System.out.printf("F:%f%n", f);
		
		
		
		return errRate; 
	}
	
	public static double evalBinaryClassifier(BasicDataset dataset, BinaryClassifier<Double> classifier){
		int misclassified = 0;
		int tp = 0;
		int tn = 0;
		int fp = 0;
		int fn = 0;
		int totPositive = 0;
		
		
		
		for(int i = 0; i != dataset.size(); i++){
			if(classifier.classify(dataset.get(i)) != dataset.target(i)){
				misclassified++;
			
				if(dataset.target(i) == 1)
					fn++;
				else
					fp++;
			}
				
			else{
				if(dataset.target(i) == 1)
					tp++;
				else
					tn++;
			}
			
			if(dataset.target(i) == 1)
				totPositive++;
			
		}
		
		double errRate = 1.0*misclassified/dataset.size();
		double precision = 1.0*tp/(tp + fp);
		double recall = 1.0*tp/(tp + fn);
		
		double f = 2* precision * recall /(precision + recall);
		
		System.out.printf("Total positives/negatives: %d/%d%n", totPositive, dataset.size() - totPositive);
		System.out.printf("Classifier error:%f%n",errRate);
		System.out.printf("Error: %d/%d%n", misclassified, dataset.size());
		System.out.printf("True positives/negatives: %d/%d%n", tp, tn);
		System.out.printf("False positives/negatives: %d/%d%n", fp, fn);
		System.out.printf("Precision/Recall:%f/%f%n", precision, recall);
		System.out.printf("F:%f%n", f);
		
		
		
		return errRate; 
	}

	
	public static void plotROC(BinaryClassifier<Double> classifier, BasicDataset dataset, String filename){
		
	}



		

	
		

}
