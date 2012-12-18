package ru.iitp.proling.svm;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import name.kazennikov.ml.core.Instance;
import name.kazennikov.ml.core.MulticlassProblem;
import name.kazennikov.ml.core.MulticlassSolver;

import cc.mallet.types.SparseVector;

import ru.iitp.proling.ml.core.MultiScorer;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.svm.kernel.Kernel;

public class MulticlassOVOSolver implements MulticlassSolver {
	
	double c = 1;
	BinarySolver solver;
	Kernel kernel;
	
	public MulticlassOVOSolver(double c, BinarySolver solver, Kernel kernel){
		this.c = c;
		this.solver = solver;
		this.kernel = kernel;
	}


	@Override
	public MultiScorer solve(MulticlassProblem problem) {
		OVOMultiScorer scorer = new OVOMultiScorer(problem.classes(), kernel);
	
		for(int i = 0; i != problem.classes(); i++){
			for(int j = i + 1; j != problem.classes(); j++){
				// build i against j classifier;
				//double[] targets = new double[problem.size()];
				// TODO: add suitable scorer
				TDoubleArrayList targets = new TDoubleArrayList(problem.size());
				List<Instance> instances = new ArrayList<Instance>();
				for(int k = 0; k != problem.size(); k++){
					int current = problem.target(k);
					if(!(current == i || current == j))
						continue;
					targets.add(current == i? 1 : -1);
					instances.add(problem.get(k));
				}
				WeightVector wv = new WeightVectorMallet(instances, targets.toArray(), kernel);
				solver.solve(wv);
				scorer.addPredictor(wv.vec(), new Labels(i, j));

			}
		}
		
		return scorer;
	}
	
	static class Labels{
		public final int positive;
		public final int negative;
		
		public Labels(int pos, int neg){
			this.positive = pos;
			this.negative = neg;
		}
	}
	
	static class OVOMultiScorer extends MultiScorer{
		
		int nClasses; // number of classes
		List<double[]> w = new ArrayList<double[]>();
		List<Labels> labels = new ArrayList<Labels>();
		TIntObjectHashMap<TIntArrayList> classes = new TIntObjectHashMap<TIntArrayList>(); // map class -> indexes in {w, labels}
		Kernel kernel;
		

		
		public OVOMultiScorer(int nClasses, Kernel kernel){
			this.nClasses = nClasses;
			this.kernel = kernel;
			for(int i = 0; i != nClasses; i++)
				classes.put(i, new TIntArrayList());
			
			
		}
		
		public void addPredictor(double[] w, Labels labels){
			int index = this.w.size();
			this.w.add(w.clone());
			this.labels.add(labels);
			classes.get(labels.positive).add(index);
			classes.get(labels.negative).add(index);
		}
		
		
		
		
		
		

		@Override
		public double score(Instance x, int index) {
			TIntArrayList indexes = classes.get(index);
			int score = 0;
			
			for(int i = 0; i != indexes.size(); i++){
				Labels label = labels.get(indexes.get(i)); 
				double s = kernel.dot(w.get(indexes.get(i)), x);
				int cls = s > 0? label.positive : label.negative;
				
				if(cls == index)
					score++;
			}
			
			return score;
		}

		@Override
		public double score(SparseVector x, int index) {
			TIntArrayList indexes = classes.get(index);
			int score = 0;
			
			for(int i = 0; i != indexes.size(); i++){
				Labels label = labels.get(indexes.get(i)); 
				double s = kernel.dot(w.get(indexes.get(i)), x);
				int cls = s > 0? label.positive : label.negative;
				
				if(cls == index)
					score++;
			}
			
			return score;
		}

		@Override
		public double[] score(SparseVector x) {
			double[] scores = new double[nClasses];
			
			for(int i = 0; i != w.size(); i++){
				double[] vec = w.get(i);
				Labels label = labels.get(i);
				double score = kernel.dot(vec, x);
				
				scores[score > 0? label.positive : label.negative] += 1;
			}
			
			return scores;
		}
		
		@Override
		public double[] score(Instance x) {
			double[] scores = new double[nClasses];
			
			for(int i = 0; i != w.size(); i++){
				double[] vec = w.get(i);
				Labels label = labels.get(i);
				double score = kernel.dot(vec, x);
				
				scores[score > 0? label.positive : label.negative] += 1;
			}
			
			return scores;
		}


		@Override
		public int size() {
			return nClasses;
		}
		
	}

}
