package ru.iitp.proling.ml.scorer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.svm.BinaryClassifier;

import cc.mallet.types.SparseVector;


/**
 * A scorer object. It returns a single score for an input vector.
 * Typical usage: binary classification and ranking. 
 * @author ant
 *
 */
public abstract class Scorer implements Serializable{
	public abstract double score(Instance v);
	public abstract double score(SparseVector v);
	
	@SuppressWarnings("unchecked")
	public static <T> Scorer read(String filename) throws IOException, ClassNotFoundException{
		
		FileInputStream fos = new FileInputStream(filename);
		ObjectInputStream oos = new ObjectInputStream(fos);
		Scorer scorer = (Scorer) oos.readObject(); 
		oos.close();
		fos.close();
		
		return scorer;
	}
	
	public void write(String filename) throws IOException{
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
		fos.close();
	}

}
