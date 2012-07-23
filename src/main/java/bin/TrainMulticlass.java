package bin;


import gnu.trove.list.array.TIntArrayList;

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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import ru.iitp.proling.common.MurmurHash;
import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.MulticlassProblem;
import ru.iitp.proling.ml.core.SimpleInstance;
import ru.iitp.proling.ml.dataset.BinaryDatasetReaderDouble;
import ru.iitp.proling.ml.dataset.BinaryModels;
import ru.iitp.proling.ml.scorer.Scorer;
import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.MulticlassCSSolver;
import ru.iitp.proling.svm.MulticlassProblemBasic;
import ru.iitp.proling.svm.RWSample;
import ru.iitp.proling.svm.SVMBinarySolver;
import ru.iitp.proling.svm.WeightVectorLinear;
import ru.iitp.proling.svm.WeightVectorRanking;
import ru.iitp.proling.svm.kernel.LinearKernel;


public class TrainMulticlass {
	public static class MCSolver extends MulticlassCSSolver.AbstractHashMCCS {

		public MCSolver(MulticlassProblem problem, int dim, double c, double eps, int maxIter) {
			super(problem, dim, c, eps, maxIter);
		}

		@Override
		public int h(int v, int x) {
			long z = v;
			z <<= 32;
			z += x;
			return (MurmurHash.hash(z, 0xcafebabe) >>> 1) % dim();
		}
		
	}
	
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
		
		
		BinaryDatasetReaderDouble dsReader = 
				new BinaryDatasetReaderDouble(new BufferedInputStream(Utils.openReadFile(optSet.nonOptionArguments().get(0))));
		
		List<Instance> instances = new ArrayList<Instance>();
		TIntArrayList targets = new TIntArrayList();

		int counter = 0;
		while(dsReader.hasNext()) {
			if(++counter % 10000 == 0) {
				System.out.printf("%d...", counter);
				System.out.flush();
			}
			int target = dsReader.target();
			int[] indexes = dsReader.indexes();
			double[] values = dsReader.values();
			targets.add(target - 1);
			instances.add(new SimpleInstance(indexes, values));
		}
		
		System.out.printf("Done.%n");
		dsReader.close();
		
		MulticlassProblemBasic p = new MulticlassProblemBasic(instances, targets.toNativeArray(), new LinearKernel());
		//MulticlassCSSolver.MulticlassCS solver = new MulticlassCSSolver.MulticlassCS(p, optSet.valueOf(opC), optSet.valueOf(opEps), 500);
		MulticlassCSSolver.AbstractHashMCCS solver = new MCSolver(p, 1 << 22, optSet.valueOf(opC), optSet.valueOf(opEps), 500);
		solver.solve();
		
		
		System.out.println("Dataset size:" + Integer.toString(instances.size()));
		//System.out.println("Dataset dim:" + Integer.toString(dset.dim()));
		System.out.printf("Loss:%f%n", solver.zero_one_loss());
		//System.out.printf("Dual objective:%f%n", solver.dual_objective());
		
		
		if(optSet.has(opModel)) {
			OutputStream os = new BufferedOutputStream(Utils.openWriteFile(optSet.valueOf(opModel)));
			BinaryModels.writeVector(os, solver.w());
			os.close();
		}


	}


}
