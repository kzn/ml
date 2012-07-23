import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import ru.iitp.proling.ml.boosting.AdaBoost;
import ru.iitp.proling.ml.boosting.LinearSVMWeakLearner;
import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.SimpleDataset;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.BinaryClassifier;
import ru.iitp.proling.svm.ClassifierEval;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.WeightVectorMallet;
import ru.iitp.proling.svm.kernel.Kernel;
import ru.iitp.proling.svm.kernel.LinearKernel;
import ru.iitp.proling.svm.kernel.PolyHashKernel;


public class TestSelf {
	public static List<cc.mallet.types.Instance> readInsts(String filename) throws IOException, ClassNotFoundException{
		InputStream s = new FileInputStream(filename);
		if(filename.endsWith(".gz"))
			s = new GZIPInputStream(s);
		ObjectInputStream ois = new ObjectInputStream(s);
		List<cc.mallet.types.Instance> insts = new ArrayList<cc.mallet.types.Instance>();
		try{
			while(true){
				cc.mallet.types.Instance inst = (cc.mallet.types.Instance)ois.readObject();
				if(inst == null)
					break;
				insts.add(inst);
			}
		}
		catch(EOFException e){}
		s.close();
		
		return insts;

	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if(args.length == 0){
			System.out.println("Usage: program filename");
			return;
		}

		List<cc.mallet.types.Instance> insts = readInsts(args[0]);
		SimpleDataset ds = SimpleDataset.convert(insts);
		int dim = 0;
		for(Instance inst : ds.instances())
			dim = Math.max(dim, inst.dim());
		
		
		Kernel k = new PolyHashKernel(0.1, 0.1, dim, 1000000);
		k = new LinearKernel();
		//WeightVector wv = new WeightVectorMallet(ds.instances(), ds.targets(), k);
		DCDSolver.verbosity = 5;
		//AdaBoost ab = new AdaBoost(new LinearSVMWeakLearner(5000, 5000, 5000), 5);
		
		Scorer sc = DCDSolver.solve(ds.instances(), ds.targets(), 0.05, 0.05, 20000, 0.1, 200000, 1);
		//Scorer sc = ab.train(ds.instances(), ds.targets());
		//Scorer sc = DCDSolver.solve(wv, 0.05, 0.05, 500, 0.1, 500000, 1);
		BinaryClassifier<Double> cl = new BinaryClassifier<Double>(sc, 1.0, -1.0);
		System.out.println("\nTrain:\n");
		ClassifierEval.evalBinaryClassifierMallet(ds.instances(), ds.targets(), cl);
		
		if(args.length > 1){
			System.out.println("\nTest:\n");
			List<cc.mallet.types.Instance> instsTest = readInsts(args[1]);
			SimpleDataset dsTest = SimpleDataset.convert(instsTest);
			ClassifierEval.evalBinaryClassifierMallet(dsTest.instances(), dsTest.targets(), cl);
		}
		
		cl.write(args[0] + ".model");
		



	}

}
