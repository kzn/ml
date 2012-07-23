import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;


import gnu.trove.TDoubleArrayList;
import gnu.trove.TIntArrayList;
import ru.iitp.proling.ml.core.WeightVector;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.ClassifierEval;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.RankBoost;
import ru.iitp.proling.svm.SVMBinarySolver;
import ru.iitp.proling.svm.SimpleRanker;
import ru.iitp.proling.svm.WeightVectorLinear;
import ru.iitp.proling.svm.WeightVectorRanking;
import ru.iitp.proling.svm.kernel.LinearKernel;


public class TestRanking {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		OptionParser op = new OptionParser();
		OptionSpec<Integer> opIters = op.accepts("iters").withRequiredArg().ofType(Integer.class).defaultsTo(500);
		OptionSpec<Double> opC = op.accepts("c").withRequiredArg().ofType(Double.class).defaultsTo(1.0);
		OptionSpec<Double> opEps = op.accepts("eps").withRequiredArg().ofType(Double.class).defaultsTo(0.1);
		OptionSpec<String> opModel = op.accepts("model").withRequiredArg().ofType(String.class);
		OptionSpec<Integer> opVerbose = op.accepts("verbose").withRequiredArg().ofType(Integer.class).defaultsTo(5);
		op.accepts("?");
		
		OptionSet optSet = op.parse(args);
		
		boolean hasTest = optSet.nonOptionArguments().size() > 1;
		
		if(optSet.nonOptionArguments().size() == 0 || optSet.has("?")){
			op.printHelpOn(System.out);
			return;
		}
		
		
		BasicDataset dset = BasicDataset.readText(optSet.nonOptionArguments().get(0));
		BasicDataset dtest = null;
		
		if(hasTest){
			dtest = BasicDataset.readText(optSet.nonOptionArguments().get(1));
		}
		
		List<Integer> qids = new ArrayList<Integer>(dset.size()); 
		double[] targets = new double[dset.size()];
		
		for(int i = 0; i != dset.size(); i++){
			qids.add(dset.get(i).qid);
			targets[i] = dset.alphabet().get(dset.targets()[i]);
		}
		
		
		WeightVectorRanking wv = new WeightVectorRanking(new WeightVectorLinear(dset.instances(), targets, new LinearKernel()), qids);
		
		
		DCDSolver.verbosity = optSet.valueOf(opVerbose);
		Scorer s = DCDSolver.solve(wv, optSet.valueOf(opC), optSet.valueOf(opC), optSet.valueOf(opIters), optSet.valueOf(opEps), 1000000, 1);
		//ru.iitp.proling.ml.scorer.Scorer s = RankBoost.train(wv, new DCDSolver(10, 10, 100, 0.1, 500000), 300);
		
		SimpleRanker sr = new SimpleRanker(s);
		
		System.out.println("Dataset size:" + Integer.toString(dset.size()));
		System.out.println("Dataset dim:" + Integer.toString(dset.dim()));
		System.out.printf("Loss:%f%n", SVMBinarySolver.loss(wv));
		System.out.printf("Dual objective:%f%n", SVMBinarySolver.objectiveDual(wv));
		
		
		//System.out.printf("Errors on test: %d/%d%n", errs, dtest.size());
		ClassifierEval.evalRanker(dset, sr);
		if(dtest != null)
			ClassifierEval.evalRanker(dtest, sr);
		
		if(optSet.has(opModel))
			s.write(optSet.valueOf(opModel));
			
		
		



	}

}
