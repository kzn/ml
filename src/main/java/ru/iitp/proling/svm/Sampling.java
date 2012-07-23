package ru.iitp.proling.svm;

import java.util.Arrays;
import java.util.Random;

public class Sampling {
	
	/**
	 * Compute sample from uniform distribution
	 * @param n size of the source dataset
	 * @param size size of resulting subsample
	 * @param r random number generator
	 * @return
	 */
	public static int[] uniformSampling(int n, int size, Random r){
		int[] dest = new int[size];
		
		for(int i = 0; i < size; i++)
			dest[i] = r.nextInt(n);
		
		return dest;
	}
	
	/**
	 * Compute sample from  weight distribution
	 * @param weights weight of each sample in the dataset. Assume that dataset.size() = weights.length
	 * @param size size of resulting subsample
	 * @param r random number generator
	 * @return
	 */
	public static int[] weightedSamplingBulk(double[] weights, int size, Random r){
		int[] dest = new int[size];
		
		double[] wEnds = new double[weights.length];
		double[] randoms = new double[size];
		double sum = 0;
		// compute ends 
		for(int i = 0; i < weights.length; i++){
			sum += weights[i];
			wEnds[i] = sum;
		}
		for(int i = 0; i < size; i++)
			randoms[i] = r.nextDouble();
		
		Arrays.sort(randoms);
		int endIndex = 0;
		
		for(int i = 0; i < size; i++){
			while(endIndex < wEnds.length && randoms[i] >= wEnds[endIndex])
				endIndex++;
			
			dest[i] = endIndex;
			
		}
		
		return dest;
	}
	
	
	public static void main(String[] args){
		double[] weights = new double[] {0.1, 0.4, 0.5};
		
		int[] s = weightedSamplingBulk(weights, 1000, new Random());
		int ones = 0;
		for(int i : s)
			if(i == 0)
				ones++;
		
		System.out.println("w:" + ones);
		
	}



}
