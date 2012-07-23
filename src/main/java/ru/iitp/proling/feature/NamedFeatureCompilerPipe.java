package ru.iitp.proling.feature;

import java.util.ArrayList;
import java.util.List;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;

/**
 * Converts List<NamedFeature> -> List<Feature>
 * @author ant
 *
 */
public class NamedFeatureCompilerPipe extends Pipe {
	Alphabet alphabet;
	
	
	public NamedFeatureCompilerPipe(Alphabet alphabet){
		this.alphabet = alphabet;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Instance pipe(Instance carrier){
		
		List<NamedFeature> namedFeats = (List<NamedFeature>)carrier.getData();
		List<Feature> feats = new ArrayList<Feature>(namedFeats.size());
		
		for(NamedFeature f : namedFeats){
			feats.add(f.compile(alphabet));
		}
		
		carrier.setData(feats);
		return carrier;
	}

}
