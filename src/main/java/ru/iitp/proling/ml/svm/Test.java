package ru.iitp.proling.ml.svm;

import gnu.trove.list.array.TIntArrayList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.iitp.proling.ml.core.DatasetUtils;
import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.MulticlassProblem;
import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.MulticlassCSSolver;
import ru.iitp.proling.svm.MulticlassProblemBasic;

public class Test {
	
	public static class CSSolver extends AbstractHKMulticlassCS {
		int sdim;

		public CSSolver(MulticlassProblem problem, int dim, double c, double eps, int maxiter, int sdim) {
			super(problem, dim, c, eps, maxiter);
			this.sdim = sdim;
		}

		@Override
		public int h(int v, int x) {
			return v * sdim + x;
		}
		
		public int predict(Instance x) {
			double score = Double.NEGATIVE_INFINITY;
			int cls = 0;
			for(int c = 0; c < problem.classes(); c++) {
				double z = 0;
				for(int i = 0; i < x.size(); i++) {
					z += w[h(c, x.indexAt(i))] * x.valueAt(i);
				}
				
				if(z > score) {
					score = z;
					cls = c;
				}
			}
			
			return cls;
		}
	}
	
	public static class Solver1 extends DCDMCLinearHK {

		int sdim;
		public Solver1(List<Instance> instances, int[] targets, int dim, int numClasses, double c_pos, double c_neg, int iter, double eps,
				int threshold, int sdim) {
			super(instances, targets, dim, numClasses, c_pos, c_neg, iter, eps, threshold);

			this.sdim = sdim;
		}

		@Override
		public int h(int c, int index) {

			return c * sdim + index;
		}
		
		public int predict(Instance x) {
			double score = Double.NEGATIVE_INFINITY;
			int cls = 0;
			for(int c = 0; c < numClasses()
					; c++) {
				double z = 0;
				for(int i = 0; i < x.size(); i++) {
					z += w[h(c, x.indexAt(i))] * x.valueAt(i);
				}
				
				if(z > score) {
					score = z;
					cls = c;
				}
			}
			
			return cls;
		}

		
	}
	
	public static void main(String[] args) throws IOException {
		BasicDataset ds = BasicDataset.readText("dna.scale");
		TIntArrayList targets = new TIntArrayList();
		List<Instance> instances = new ArrayList<Instance>();
		
		for(int i = 0; i != ds.size(); i++) {
			targets.add((int)ds.target(i));
			instances.add(ds.get(i));
		}
		
		MulticlassProblemBasic p = new MulticlassProblemBasic(instances, targets.toArray());
		
		Solver1 solver = new Solver1(instances, targets.toArray(), (DatasetUtils.dim(instances) + 1)*(p.classes() + 1), p.classes(), 0.1, 0.1, 500, 0.1, 0, DatasetUtils.dim(instances));
		//CSSolver solver = new CSSolver(p, (DatasetUtils.dim(instances) + 1)*(p.classes() + 1), 0.1, 0.1, 500, DatasetUtils.dim(instances));
		solver.setVerbosity(5);
		solver.solve(1);
		
		
		BasicDataset dsTest = BasicDataset.readText("dna.scale.t");
		int errs = 0;
		for(int i = 0; i != dsTest.size(); i++) {
			int c = solver.predict(dsTest.get(i));
			if(c != (int)dsTest.target(i)) {
				errs++;
			}
		}
		
		System.out.printf("Test Error: %d/%d%n", errs, dsTest.size());
		
		
		
	}

}
