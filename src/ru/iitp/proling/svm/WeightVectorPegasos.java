package ru.iitp.proling.svm;

import java.util.Arrays;

public class WeightVectorPegasos extends WeightVectorProxy {
	protected double k;
	protected double sqnorm;
	
	public WeightVectorPegasos(WeightVector base){
		super(base);
		k = 1;
		sqnorm = 0;
	}
	
	
	public double alpha(int idx){
		return w.alpha(idx)*k;
	}
	
	public void add_alpha(int idx, double factor){
		w.add_alpha(idx, factor/k);
	}
	
	public void add(int idx, double factor){
		sqnorm += w.snorm(idx) * factor * factor;
	    sqnorm += 2 * factor * dot(idx);
		w.add(idx, factor/k);
	}
	
	  // norm
	public double norm(){
	    return Math.sqrt(sqnorm);	
	}

	public double snorm(){
		return sqnorm;
	}

	 public void scale(double factor){
		 sqnorm *= factor * factor;
	     k *= factor;
	  }


	  public double dot(int idx){
	    return w.dot(idx)*k;
	  }
	  
	  public double[] vec(){
		  double v[] = new double[w.dim()];
		  double u[] =  w.vec();
		  		  
		  for(int i = 0; i != v.length; i++)
			  v[i] = u[i] * k;
		  
		  return v;
	  }

	

}
