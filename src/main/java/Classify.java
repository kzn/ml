import java.io.IOException;

import ru.iitp.proling.svm.BasicDataset;
import ru.iitp.proling.svm.BinaryClassifier;
import ru.iitp.proling.svm.ClassifierEval;


public class Classify {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if(args.length < 2){
			System.out.println("Usage: classify dataset model");
			return;
		}
		
		BasicDataset dataset = BasicDataset.readBinary(args[0]);
		BinaryClassifier<Double> classifier = BinaryClassifier.read(args[1]);
		ClassifierEval.evalBinaryClassifier(dataset, classifier);

	}

}
