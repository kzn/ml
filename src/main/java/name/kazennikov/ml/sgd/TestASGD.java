package name.kazennikov.ml.sgd;

import gnu.trove.list.array.TDoubleArrayList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import name.kazennikov.ml.core.Instance;
import name.kazennikov.ml.svm.DCDLinear;

import ru.iitp.proling.svm.BasicDataset;

public class TestASGD {
	public static class Dataset {
		List<Instance> x = new ArrayList<Instance>();
		TDoubleArrayList y = new TDoubleArrayList();
		
		public int dim() {
			int dim = 0;
			for(int i = 0; i < x.size(); i++) {
				dim = Math.max(dim, x.get(i).dim());
			}
			
			return dim + 1;
		}
		
		public int size() {
			return x.size();
		}
		
		public double test(double[] w, LossFunction lossFun) {
			double loss = 0;
			for(int i = 0; i < x.size();i ++) {
				Instance s = x.get(i);
				double target = y.get(i);
				double score = 0;
				
				for(int j = 0; j < s.size(); j++) {
					if(s.indexAt(j) < w.length) {
						score += w[s.indexAt(j)] * s.valueAt(j);
					}
				}
				
				loss += lossFun.loss(score, target);
				
			}
			
			return loss;
		}

	}
	
	public static Dataset readDataset(File f) throws IOException {
		BasicDataset dset = BasicDataset.readText(f.getAbsolutePath());
		Dataset d = new Dataset();
		
		for(int i = 0; i < dset.size(); i++) {
			d.x.add(dset.get(i));
			d.y.add(dset.target(i));
		}
		
		return d;
	}
	
	
	public static void main(String[] args) throws IOException {
		Dataset train = readDataset(new File("train.dat"));
		Dataset test = readDataset(new File("test.dat"));
		
		DCDLinear dcd = new DCDLinear(train.x, train.y.toArray(), 2, 2, 500, 0.1, 0);
		ASGD asgd = new ASGD(new LossFunctions.HingeLoss(), 1.0e-5, 1 * train.size(), 0.25, train.dim());
//		ASGD asgd = new ASGD(new LossFunctions.HingeLoss(), 1.0/0.1/train.size(), 2 * train.size(), 0.05, train.dim());
		for(int i = 0; i < 10; i++) {
			asgd.train(0, train.x.size(), train.x, train.y);
			System.out.printf("Cost: %.4f/%.4f/%.4f%n%n", 
					train.test(asgd.a(), new LossFunctions.HingeLoss())/train.size(),
					test.test(asgd.a(), new LossFunctions.HingeLoss())/test.size(),
					test.test(asgd.w(), new LossFunctions.HingeLoss())/test.size()
					);
		}
		dcd.solve(1);
		
		
		double[] w = asgd.a();
		
		System.out.printf("Errors: %f.4%n", test.test(w, new LossFunctions.HingeLoss())/test.size());
		System.out.printf("|w| = %.4f%n", DenseVector.dot(w, w));
	}

}
