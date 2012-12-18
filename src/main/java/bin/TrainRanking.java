package bin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import name.kazennikov.ml.dataset.BinaryDatasetReaderDouble;
import name.kazennikov.ml.dataset.BinaryModels;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.RWSample;
import ru.iitp.proling.svm.SVMBinarySolver;
import ru.iitp.proling.svm.WeightVectorLinear;
import ru.iitp.proling.svm.WeightVectorRanking;
import ru.iitp.proling.svm.kernel.LinearKernel;

public class TrainRanking {
	
	

	public static void main(String[] args) throws IOException {
		BasicConfigurator.configure();
		if(new File("log4j.properties").exists())
			PropertyConfigurator.configure("log4j.properties");
		OptionParser op = new OptionParser();
		op.accepts("help", "Show help");


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
		
		//BasicDataset dset = BasicDataset.readText(optSet.nonOptionArguments().get(0));
		
		BasicDataset dset = new BasicDataset();
		
		BinaryDatasetReaderDouble dsReader = 
				new BinaryDatasetReaderDouble(new BufferedInputStream(Utils.openReadFile(optSet.nonOptionArguments().get(0))));

		int counter = 0;
		while(dsReader.hasNext()) {
			if(++counter % 10000 == 0) {
				System.out.printf("%d...", counter);
				System.out.flush();
			}
			int target = dsReader.target();
			int qid = dsReader.qid();
			int[] indexes = dsReader.indexes();
			double[] values = dsReader.values();

			dset.add(new RWSample<Double>(Double.valueOf(target), indexes, values, 1.0, qid));
		}
		System.out.printf("Done.%n");
		dsReader.close();
		
		List<Integer> qids = new ArrayList<Integer>(dset.size()); 
		double[] targets = new double[dset.size()];
		int[] dsetTargets = dset.targets();
		for(int i = 0; i != dset.size(); i++){
			qids.add(dset.get(i).qid);
			
			targets[i] = dset.alphabet().get(dsetTargets[i]);
		}
		
		
		WeightVectorRanking wv = new WeightVectorRanking(new WeightVectorLinear(dset.instances(), targets, new LinearKernel()), qids);
		
		
		DCDSolver.verbosity = optSet.valueOf(opVerbose);
		Scorer s = DCDSolver.solve(wv, optSet.valueOf(opC), optSet.valueOf(opC), optSet.valueOf(opIters), optSet.valueOf(opEps), 1000000, 1);
		System.out.println("Dataset size:" + Integer.toString(dset.size()));
		System.out.println("Dataset dim:" + Integer.toString(dset.dim()));
		System.out.printf("Loss:%f%n", SVMBinarySolver.loss(wv));
		System.out.printf("Dual objective:%f%n", SVMBinarySolver.objectiveDual(wv));
		
		
		if(optSet.has(opModel)) {
			OutputStream os = new BufferedOutputStream(Utils.openWriteFile(optSet.valueOf(opModel)));
			BinaryModels.writeVector(os, wv.vec());
			os.close();
		}


	}

}
