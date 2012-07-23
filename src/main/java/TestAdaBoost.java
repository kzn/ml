import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.Target2Label;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.InstanceList;

import ru.iitp.proling.ml.boosting.AdaBoostReal;
import ru.iitp.proling.ml.boosting.CappedLinearSVMLearner;
import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.BinaryClassifier;

import ru.iitp.proling.svm.ClassifierEval;



public class TestAdaBoost {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String filename = args[0];
		
		BasicDataset ds = BasicDataset.readText(filename);
		List<Instance> inst = new ArrayList<Instance>();
		double[] targets = new double[ds.size()];
		Alphabet d = new Alphabet();
		
		for(int i = 0; i != ds.dim() + 1; i++)
			d.lookupIndex(Integer.valueOf(i));
		
		Pipe p = new Target2Label();
		InstanceList il = new InstanceList(p);
			
		
		for(int i = 0; i != ds.size(); i++){
			il.addThruPipe(new cc.mallet.types.Instance(new FeatureVector(d, ds.get(i).indexes()), Double.valueOf(ds.target(i)), null, null));
			
			inst.add(ds.get(i));
			targets[i] = ds.target(i);
		}
		
		/*AdaBoostTrainer tr = new AdaBoostTrainer(new C45Trainer(2), 20);
		//C45Trainer tr = new C45Trainer(8, false);
		
		AdaBoost c = tr.train(il);
		System.out.printf("Accuracy: %f%n", c.getAccuracy(il));*/
		
		
		
		AdaBoostReal ab = new AdaBoostReal(new CappedLinearSVMLearner(100, 100, 1000), 100);
		
		Scorer s = ab.train(inst, targets, 1, 1);
		
		ClassifierEval.evalBinaryClassifier(ds, new BinaryClassifier<Double>(s, 1.0, -1.0));
		
		if(args.length == 2){
			BasicDataset dtest = BasicDataset.readText(args[1]);
			ClassifierEval.evalBinaryClassifier(dtest, new BinaryClassifier<Double>(s, 1.0, -1.0));
		}
			
		
		

	}

}
