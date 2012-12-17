import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.iitp.proling.ml.core.Instance;
import ru.iitp.proling.ml.core.MultiScorer;
import ru.iitp.proling.ml.core.MulticlassProblem;
import ru.iitp.proling.ml.core.MulticlassSolver;
import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.DCDSolver;
import ru.iitp.proling.svm.MulticlassCSSolver;
import ru.iitp.proling.svm.MulticlassOVOSolver;
import ru.iitp.proling.svm.MulticlassOVRSolver;
import ru.iitp.proling.svm.MulticlassProblemBasic;
import ru.iitp.proling.svm.kernel.LinearKernel;


public class TestMulticlass {
	public static void main(String[] args) throws IOException{
		
		if(args.length > 0)
			System.out.println(args[0]);
		else{
			System.out.println("Usage: program [filename]");
			return;
		}
		
		
		
		BasicDataset dset = BasicDataset.readText(args[0]);
		BasicDataset dtest = BasicDataset.readText(args[1]);
		List<Instance> data = new ArrayList<Instance>();
		for(int i = 0; i != dset.size(); i++)
			data.add(dset.get(i));
		
		MulticlassProblem p = new MulticlassProblemBasic(data, dset.targets());
		
		//MulticlassCSSolver solver = new MulticlassCSSolver(0.1, 0.1, 5000);
		MulticlassSolver solver = new MulticlassOVOSolver(0.1, new DCDSolver(0.1, 0.1, 500, 0.1, 500000), new LinearKernel());
		MultiScorer s = solver.solve(p);
		int errs = 0;
		for(int i = 0; i != dtest.size(); i++){
			double[] scores = s.score(dtest.get(i));
			int cls = -1;
			double score = Double.NEGATIVE_INFINITY;
			// argmax
			for(int j = 0; j != scores.length; j++)
				if(scores[j] > score){
					score = scores[j];
					cls = j;
				}
			
			if(!dset.alphabet().get(cls).equals(dtest.alphabet().get(dtest.targets()[i])))
				errs++;
		}
		System.out.printf("Errors on test dataset:%d/%d%n", errs, dtest.size());
		
		

	}

}
