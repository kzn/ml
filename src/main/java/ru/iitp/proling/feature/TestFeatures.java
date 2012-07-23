package ru.iitp.proling.feature;

import java.util.ArrayList;
import java.util.List;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;

public class TestFeatures {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		List<Feature> feats = new ArrayList<Feature>();
		feats.add(new NumericFeature(1, 2.0));
		feats.add(new NominalFeature(2, "abc"));
		//feats.add(new NominalFeature(2, "def"));
		feats.add(new ListFeature(3, "a", "b", "c"));
		
		Instance inst = new Instance(feats, Integer.valueOf(1), null, null);
		List<Pipe> pipes = new ArrayList<Pipe>();
		pipes.add(new ListFeatureCompilerPipe());
		pipes.add(new CompileFeatureList());
		pipes.add(new SimpleEncoderPipe(new Alphabet()));
		for(Pipe p : pipes)
			inst = p.pipe(inst);
		
		
		int j = 4;
		
		

		
		


	}

}
