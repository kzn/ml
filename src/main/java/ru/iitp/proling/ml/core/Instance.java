package ru.iitp.proling.ml.core;


/**
 * Generic interface for single unlabeled instance.
 * Introduced, at this naming is simple at in MALLET for example.
 * @author ant
 *
 */
public interface Instance {
	
	public int indexAt(int index);
	public double valueAt(int index);
	public int size();
	public int dim();
}
