package ru.iitp.proling.svm;

import java.util.List;

import name.kazennikov.ml.core.Instance;
import name.kazennikov.ml.core.MulticlassProblem;

import ru.iitp.proling.svm.kernel.Kernel;
/**
 * Basic
 * @author ant
 *
 */
public class MulticlassProblemBasic extends MulticlassProblem {
	
	final List<Instance> data;
	double[] snorms;
	int[] targets;
	int dim = 0;
	int classes = 0;
	
	public MulticlassProblemBasic(List<Instance> data, int[] targets){
		this.data = data;
		this.targets = targets;
		snorms = new double[data.size()];
		
		int i = 0;
		for(Instance inst : data){
			dim = Math.max(dim, inst.dim());
			classes = Math.max(classes, targets[i]);
			snorms[i] = 0;//FIXME////kernel.snorm(inst);
			i++;
		}
		classes++; // as class index may start at 0
		
	}

	@Override
	public int classes() {
		return classes;
	}

	@Override
	public Instance get(int index) {
		return data.get(index);
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public int target(int index) {
		return targets[index];
	}
	
	@Override
	public int dim(){
		return dim;
	}
	
	@Override
	public double snorm(int index){
		return snorms[index];
	}
}
