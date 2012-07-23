package ru.iitp.proling.feature;

import java.util.ArrayList;
import java.util.List;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;

/**
 * Compile List/Set features to Nominal
 * @author ant
 *
 */
public class ListFeatureCompilerPipe extends Pipe{
	Alphabet compAlphabet;
	Alphabet compFeatures;
	
	public ListFeatureCompilerPipe() {
		compAlphabet = new Alphabet(Long.class);
		compFeatures = new Alphabet(Object.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Instance pipe(Instance carrier){
		List<Feature> newFeats = new ArrayList<Feature>();
		
		for(Feature f : (List<Feature>)(carrier.getData())){
			if(!(f instanceof ListFeature)){
				f.id = compAlphabet.lookupIndex(Long.valueOf(f.id())) + 1;
				newFeats.add(f);
				continue;
			}
				
			
			ListFeature lf = (ListFeature)f;
			
			for(Object o : lf.value()){
				long key = lf.id();
				key <<= 32;
				key += compFeatures.lookupIndex((Object)o);
				int featId = compAlphabet.lookupIndex(key) + 1;
				newFeats.add(new NominalFeature(featId, Integer.valueOf(1)));
			}
		}
		carrier.setData(newFeats);
		
		return carrier;
	}
	
	
	
	

}
