package ru.iitp.proling.feature;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import ru.iitp.proling.common.Alphabet;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;

/**
 * Compile list of Features(NominalFeature, NumericFeature, ListFeature) to their compiled forms
 * where all objects are indexes(Integers)
 * @author ant
 *
 */
public class CompileFeatureList extends Pipe {

	private static final long serialVersionUID = 1L;
	TIntObjectHashMap<Alphabet<Object>> alphabets = new TIntObjectHashMap<Alphabet<Object>>();
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Instance pipe(Instance carrier){

		for(Feature f : (List<Feature>)carrier.getData()){
			
			if(f instanceof NominalFeature && !(f.value() instanceof Integer))
				f.value = Integer.valueOf(getAlphabet(f.id()).get(f.value()) + 1);
			
			if(f instanceof ListFeature){
				Alphabet<Object> alphabet = getAlphabet(f.id());
				List<Integer> vals = new ArrayList<Integer>();
				
				for(Object v : ((ListFeature)f).value()){
					vals.add(Integer.valueOf(alphabet.get(v) + 1));
				}
				f.value = vals;
			}
		}
		
		return carrier;
	}
	
	
	Alphabet<Object> getAlphabet(int index){
		if(alphabets.contains(index)){
			return alphabets.get(index);
		}
		
		Alphabet<Object> alphabet = new Alphabet<Object>();
		alphabets.put(index, alphabet);		
		
		return alphabet;
		
	}


}
