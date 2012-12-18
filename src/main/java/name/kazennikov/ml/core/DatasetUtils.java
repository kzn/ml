package name.kazennikov.ml.core;

import java.util.List;


public class DatasetUtils {
	public static int dim(List<Instance> dataset) {
		int dim = 0; 
		for(Instance inst : dataset) {
			if(inst.dim() > dim) {
				dim = inst.dim();
			}
		}
		
		return dim;
	}


}
