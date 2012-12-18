package ru.iitp.proling.ml.core;


import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.List;

import name.kazennikov.ml.core.Instance;

import ru.iitp.proling.svm.BasicDataset;
import cc.mallet.types.SparseVector;

public class SimpleDataset {
	protected TDoubleArrayList targets = new TDoubleArrayList();
	protected List<Instance> instances = new ArrayList<Instance>();
	
	public double[] targets(){
		return targets.toArray();
	}
	
	public List<Instance> instances(){
		return instances;
	}
	
	public static SimpleDataset convert(BasicDataset ds){
		SimpleDataset d = new SimpleDataset();
		for(int i = 0; i != ds.size(); i++){
			d.targets.add(ds.target(i));
			d.instances.add(ds.get(i));
		}
		
		return d;
	}
	
	public static SimpleDataset convert(List<cc.mallet.types.Instance> insts){
		SimpleDataset d = new SimpleDataset();
		for(cc.mallet.types.Instance inst : insts){
			d.targets.add((Double)inst.getTarget());
			d.instances.add(new MalletInstance((SparseVector)inst.getData()));
		}
		
		return d;
	}

}
