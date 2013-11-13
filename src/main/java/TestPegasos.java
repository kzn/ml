
import gnu.trove.list.array.TDoubleArrayList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import name.kazennikov.ml.core.DatasetUtils;
import name.kazennikov.ml.core.Instance;
import name.kazennikov.ml.svm.AbstractPegasos;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import ru.iitp.proling.svm.BasicDataset;


public class TestPegasos {
	
	public static class PegagosLinear extends AbstractPegasos {
		
		public List<Instance> instances;
		public double[] targets;
		public double[] sqnorms;
		double[] w;
		double sqnorm = 0;
		double nu = 1;
		int counter = 0;
		
		public PegagosLinear(List<Instance> instances, double[] targets, int iter, double c, int k) {
			super(iter, 1, c, 1);
			this.instances = instances;
			this.targets = targets;
			w = new double[DatasetUtils.dim(instances) + 1];
			sqnorms();
		}
		

		
		public void sqnorms() {
			sqnorms = new double[instances.size()];
			for(int i = 0; i != instances.size(); i++) {
				Instance v = instances.get(i);
				for(int j = 0; j != v.size(); j++) {
					sqnorms[i] += v.valueAt(j) * v.valueAt(j);
				}
			}
		}
		

		@Override
		public void init() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int size() {
			return instances.size();
		}

		@Override
		public double target(int vec) {
			return targets[vec];
		}

		@Override
		public double dot(int vec) {
			Instance v = instances.get(vec);
			double d = 0;
			for(int i = 0; i != v.size(); i++) {
				d += w[v.indexAt(i)] * v.valueAt(i);
			}

			return d * nu;
		}

		@Override
		public void add(int vec, double factor) {
			Instance v = instances.get(vec);
			sqnorm += 2 * factor * dot(vec);
			sqnorm += sqnorms[vec] * factor * factor;

			for(int i = 0; i != v.size(); i++) {
				w[v.indexAt(i)] += factor * v.valueAt(i)/nu;
			}

			
		}

		@Override
		public void scale(double factor) {
			nu *= factor;
			sqnorm *= factor * factor;
			
		}

		@Override
		public double snorm() {
//			double d = 0; 
//			for(int i = 0; i != w.length; i++) {
//				d += w[i] * w[i];
//			}
//			
			return sqnorm;
		}
		
		public double objValue() {
			System.out.printf("Sqnorm: %f%n", snorm());

			double lambda = 1.0/c/size();
			System.out.printf("Lambda: %f%n", lambda);
			double v = 1.0/2 * snorm() * lambda;
			double l = 0;
			
			for(int i = 0; i != instances.size(); i++) {
				l += Math.max(0, 1 - target(i) * dot(i));
			}
			
			return v + l/size();
		}
		
	}

	public static void main(String[] args) throws IOException {
		BasicConfigurator.configure();
		PropertyConfigurator.configure("log4j.properties");


		
		BasicDataset dset = BasicDataset.readText("train.dat");
		TDoubleArrayList targets = new TDoubleArrayList();
		List<Instance> instances = new ArrayList<Instance>();
		for(int i = 0; i != dset.size(); i++) {
			instances.add(dset.get(i));
			targets.add(dset.target(i));
		}

		PegagosLinear solver = new PegagosLinear(instances, targets.toArray(), 10000, 1, 10);
		
		solver.solve();
		System.out.printf("Objective value: %f%n", solver.objValue());
		
		

		
		


	}

}
