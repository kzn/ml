package ru.iitp.proling.svm;

import java.util.List;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.MulticlassProblem;
import ru.iitp.proling.svm.kernel.Kernel;

public class MulticlassProblemProxy extends MulticlassProblem {
	
	final MulticlassProblem p;
	
	MulticlassProblemProxy(MulticlassProblem problem){
		this.p = problem;
	}

	@Override
	public int classes() {
		return p.classes();
	}

	@Override
	public int dim() {
		return p.dim();
	}

	@Override
	public Instance get(int index) {
		return p.get(index);
	}

	@Override
	public int size() {
		return p.size();
	}

	@Override
	public double snorm(int index) {
		return p.snorm(index);
	}

	@Override
	public int target(int index) {
		return p.target(index);
	}

	@Override
	public List<Instance> instances() {
		return p.instances();
	}

}
