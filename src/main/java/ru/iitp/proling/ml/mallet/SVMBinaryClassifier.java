package ru.iitp.proling.ml.mallet;

import ru.iitp.proling.ml.scorer.Scorer;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;

public class SVMBinaryClassifier extends Classifier{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Scorer scorer;
	
	public SVMBinaryClassifier(Pipe instancePipe, Scorer scorer){
		super(instancePipe);
		this.scorer = scorer;
		if(instancePipe.getTargetAlphabet().size() > 2)
			throw new IllegalArgumentException("Binary SVM classifier can return only 2 classes!");
		
	}
	
	@Override
	public Classification classify(Instance instance) {
		FeatureVector fv = (FeatureVector) instance.getData ();
		// Make sure the feature vector's feature dictionary matches
		// what we are expecting from our data pipe (and thus our notion
		// of feature probabilities.
		assert (instancePipe == null || fv.getAlphabet () == this.instancePipe.getDataAlphabet ());
		
		
		double score = scorer.score(fv);
		LabelAlphabet labels = getLabelAlphabet();
		
		return new Classification(instance, this, new LabelVector(labels, new double[] { -score, score} ));
	}

	
}
