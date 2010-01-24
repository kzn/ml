package ru.iitp.proling.common;

public class ArrayUtils {
	
	public static void swap(int[] array, int i, int j){
		int t = array[i];
		array[i] = array[j];
		array[j] = t;
	}
	
	
	public static void swap(double[] array, int i, int j){
		double t = array[i];
		array[i] = array[j];
		array[j] = t;
	}
	
	public static void shuffle(double[] array, int active){
		for(int i = 0; i != active; i++){
			int j = i + (int)(Math.random() * (active - i));
			swap(array, i, j);
		}
	}
	
	public static void shuffle(int[] array, int active){
		for(int i = 0; i != active; i++){
			int j = i + (int)(Math.random() * (active - i));
			swap(array, i, j);
		}
	}

}
