package ru.iitp.proling.svm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import name.kazennikov.ml.core.Instance;

import cc.mallet.types.SparseVector;
import ru.iitp.proling.ml.core.Classifier;
import ru.iitp.proling.ml.scorer.Scorer;

/**
 * Binary classification common class. Basic interface to binary predictor that has 2 outcomes.
 * This a indirect interface, as any binary classifier expected to conform to defined functions
 * @author ant
 *
 */
public class BinaryClassifier<T> implements Classifier<T>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Scorer scorer;
	T positive; // class 1
	T negative; // class 0
	double theta = 0; // point of discrimination between classes(look at classify)
	
	public BinaryClassifier(Scorer scorer, T positive, T negative){
		this.scorer = scorer;
		this.positive = positive;
		this.negative = negative;
	}
	
	public BinaryClassifier(Scorer scorer, T positive, T negative, double theta){
		this(scorer, positive, negative);
		this.theta = theta;
	}


	/**
	 * Classify vector
	 * @param v vector
	 * @return class of vector, -1 if negative in case of indicator classification
	 */
	@Override
	public T classify(Instance v) {
		return score(v) > theta? positive : negative;
	}
	
	@Override
	public T classify(SparseVector vec) {
		return score(vec) > theta? positive : negative;
	}
	
	/**
	 * Get score of vector v, by this classifier. Can be used for various generalization performance measures.
	 * @param v vector
	 * @return score 
	 */
	double score(Instance v){
		return scorer.score(v);
	}
	
	double score(SparseVector v){
		return scorer.score(v);
	}


	@Override
	public int classes() {
		return 2;
	}


	


	@Override
	public double score(Instance vec, int cls) {
		switch(cls){
		case 0:
			return -score(vec);
		case 1:
			return score(vec);
		default:
			return 0;
		}
	}


	@Override
	public double score(SparseVector vec, int cls) {
		switch(cls){
		case 0:
			return -score(vec);
		case 1:
			return score(vec);
		default:
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> BinaryClassifier<T> read(String filename) throws IOException, ClassNotFoundException{
		
		FileInputStream fos = new FileInputStream(filename);
		ObjectInputStream oos = new ObjectInputStream(fos);
		BinaryClassifier<T> classifier = (BinaryClassifier<T>) oos.readObject(); 
		oos.close();
		fos.close();
		
		return classifier;
	}
	
	public void write(String filename) throws IOException{
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
		fos.close();
	}
	
	public double theta(){
		return theta;
	}
	
	public void setTheta(double theta){
		this.theta = theta;
	}

		


}
