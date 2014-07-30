package name.kazennikov.ml.core;

import java.util.List;


import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.svm.kernel.Kernel;


/**
 * Basic multiclass problem formulation. 
 * Asserts that targets are within range [0..classes()]
 * In contrast with {@link WeightVector}, this problem formulation 
 * doesn't store the solution. So, the solution is generated (and returned)
 * by the learning algorithm itself.
 * @author ant
 *
 */
public abstract class MulticlassProblem {

	public abstract List<Instance> instances();
	public abstract int classes();
	public abstract int size();
	public abstract int dim();
	
	public abstract int target(int index);
	public abstract Instance get(int index);
	public abstract double snorm(int index);
	
	public double cost(int index){
		return 1.0;
	}
 

}
