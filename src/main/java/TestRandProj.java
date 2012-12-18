import java.io.IOException;
import java.util.Random;

import name.kazennikov.ml.core.Instance;

import cc.mallet.types.SparseVector;
import cc.mallet.util.Randoms;

import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.RWSample;


public class TestRandProj {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if(args.length < 1){
			System.out.println("Usage: program dataset");
			return;
		}
		
		int d = 256;
		
		BasicDataset dset = BasicDataset.readBinary(args[0]);
		
		double origDist[][] = new double[dset.size()][dset.size()];
		
		// compute original distances
		for(int i = 0; i != dset.size(); i++){
			for(int j = 0; i != dset.size(); i++){
				// compute distance between dset[i] and dset[j]
				RWSample<Double> instX = dset.get(i);
				RWSample<Double> instY = dset.get(j);
				
				SparseVector x = new SparseVector(instX.indexes(), instX.values(), false);
				SparseVector y = new SparseVector(instY.indexes(), instY.values(), false);
				
				SparseVector diff = x.vectorAdd(y, -1.0);
				
				origDist[i][j] = diff.twoNorm();
			}
		}
		
		double w[] = new double[d];
		Random r = new Random();
		
		for(int i = 0; i != w.length; i++)
			w[i] = r.nextGaussian();
		
		
		
		




	}
}
