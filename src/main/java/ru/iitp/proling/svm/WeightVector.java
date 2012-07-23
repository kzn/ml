package ru.iitp.proling.svm;

import ru.iitp.proling.svm.kernel.Kernel;

/**
 * Class for binary classification problem formulation.
 * Also, stores solution to forumlated problem.
 * @author ant
 *
 */
public abstract class WeightVector {
	
	/**
	 * Get dimension of weight vector
	 * @return dimension of weight vector
	 */
	public abstract int dim(); 
	
	/**
	 * Get norm of weight vector
	 * @return norm of weight vector
	 */
	public abstract double norm();
	
	/**
	 * Get squared norm of weight vector
	 * @return squared norm of weight vector
	 */
	public abstract double snorm();
	
	/**
	 * Get weight vector
	 * @return weight vector
	 */
	public abstract double[] vec();
	
	/**
	 * Scale weight vector by factor k
	 * @param k scaling factor
	 */
	public abstract void scale(double k);
	
	/** 
	 * Reset weight vector to initial state
	 */
	public abstract void clear();
	
	/**
	 * Get used kernel for learning 
	 * @return used kernel
	 */
	public abstract Kernel kernel();
	
	
	/**
	 * Get problem size - number of samples in the training dataset
	 * @return problem size
	 */
	public abstract int size(); // problem size
	/**
	 * Add a scaled vector from dataset to weight vector(w = factor * v_idx)
	 * @param idx index of vector in dataset
	 * @param factor factor of vector in the dataset
	 */
	public abstract void add(int idx, double factor);
	
	/**
	 * Add factor to alpha_idx
	 * @param idx index of alpha variable
	 * @param factor amount to add
	 */
	public abstract void add_alpha(int idx, double factor);
	
	/**
	 * Get alpha at index
	 * @param idx index
	 * @return alpha[idx] value
	 */
	public abstract double alpha(int idx);
	
	/**
	 * Get label(target) of sample idx 
	 * @param idx index of sample
	 * @return target value
	 */
	public abstract double target(int idx);
	/**
	 * Compute dot product <w, v_idx>
	 * @param idx vector index
	 * @return inner dot product <w, v_idx>
	 */
	public abstract double dot(int idx);
	
	/**
	 * Compute dot product <v_x, v_y> of vectors from dataset
	 * @param x first vector index
	 * @param y second vector index
	 * @return <v_x, v_y>
	 */
	public abstract double dot(int x, int y);
	
	/**
	 * Return squared norm of vector v_idx
	 * @param idx index of vector
	 * @return squared norm of v_idx
	 */
	public abstract double snorm(int idx);
	
	// statistics
	/**
	 * Compute loss fuction
	 * @return loss 
	 */
	public abstract double loss();
	
	/**
	 * Zero-one loss function
	 * @return zero-one loss
	 */
	public abstract double zero_one_loss();
	
	/**
	 * Compute primal SVM objective
	 * @param c regularization coefficient
	 * @return
	 */
	public abstract double objectivePrimal(double c);
	
	/**
	 * Compute dual SVM objective 
	 * @return 
	 */
	public abstract double objectiveDual();
	
	/**
	 * Compute projected gradient difference
	 * @param c regularization parameter
	 * @return
	 */
	public abstract double epsilonProjectedGradient(double c);
	/**
	 * Compute KKT coditions violation
	 * @param c regularization parameter
	 * @return
	 */
	public abstract double kktViolation(double c);
	
	/**
	 * Get cost of sample idx
	 * @param idx sample index
	 * @return cost of sample[idx]
	 */
	public double cost(int idx){
		return 1.0;
	}

}
