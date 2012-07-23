
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.MalletInstance;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.SVMBinarySolver;
import ru.iitp.proling.svm.WeightVectorMallet;
import ru.iitp.proling.svm.kernel.LinearKernel;

import cc.mallet.types.SparseVector;



public class CompileAdult {
	public static void main(String[] args) throws IOException{
		String input = args[0];
		
		BufferedReader fr = new BufferedReader(new FileReader(input));
		//Alphabet featAlphabet = new Alphabet();
		//InstanceList il = new InstanceList(new Target2Label());
		List<TObjectIntHashMap<String>> featmap = new ArrayList<TObjectIntHashMap<String>>();
		TObjectIntHashMap<String> targetmap = new TObjectIntHashMap<String>();
		List<Instance> inst = new ArrayList<Instance>();
		TDoubleArrayList tgts = new TDoubleArrayList();
		long start = System.nanoTime();
		while(true){
			String line = fr.readLine();
			if(line == null)
				break;
			String[] feats = line.split(",\\s+");
			String target = feats[feats.length - 1];
			String[] feat = Arrays.copyOf(feats, feats.length - 1);
			int i = 0;
			double[] mapped = new double[feat.length];
			int[] keys = new int[feat.length];
			for(String s : feat){
				if(featmap.size() == i)
					featmap.add(new TObjectIntHashMap<String>());
				TObjectIntHashMap<String> map = featmap.get(i);
				int key = map.get(s);
				if(key == 0){
					key = map.size() + 1;
					map.put(s, key);
				}
				mapped[i] = key;
				keys[i] = i + 1;
				i++;
			}
			int target_idx = targetmap.get(target);
			if(target_idx == 0){
				target_idx = targetmap.size() + 1;
				targetmap.put(target, target_idx);
			}
			
			inst.add(new  MalletInstance(new SparseVector(keys, mapped, false, true)));
			tgts.add(target_idx == 1? 1.0 : -1.0);
		}
		
		System.out.printf("Parsed in %f secs%n", 1.0E-9*(System.nanoTime() - start));
		WeightVectorMallet wv = new WeightVectorMallet(inst, tgts.toArray(), new LinearKernel());;
		
		DCDSolver.verbosity = 5;
		DCDSolver.solve(wv, 0.05, 0.05, 5000, 0.1, 5000, 1);
		System.out.printf("loss: %f%n",SVMBinarySolver.loss(wv));
		
		
		
		
	}
	

}
