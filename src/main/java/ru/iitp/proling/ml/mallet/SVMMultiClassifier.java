package ru.iitp.proling.ml.mallet;

import ru.iitp.proling.ml.core.MultiScorer;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;

public class SVMMultiClassifier extends Classifier{
	
	private static final long serialVersionUID = 1L;
	MultiScorer scorer;
	
	public SVMMultiClassifier(Pipe instancePipe, MultiScorer scorer){
		super(instancePipe);
		this.scorer = scorer;
	}
	
	@Override
	public Classification classify(Instance instance) {
		FeatureVector fv = (FeatureVector) instance.getData();
		// Make sure the feature vector's feature dictionary matches
		// what we are expecting from our data pipe (and thus our notion
		// of feature probabilities.
		assert (instancePipe == null || fv.getAlphabet () == this.instancePipe.getDataAlphabet ());
		
		
		double[] scores = scorer.score(fv);
		LabelAlphabet labels = getLabelAlphabet();
		
		return new Classification(instance, this, new LabelVector(labels, scores));
	}

	

}
