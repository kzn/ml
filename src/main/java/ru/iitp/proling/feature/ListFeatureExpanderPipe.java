package ru.iitp.proling.feature;

import java.util.ArrayList;
import java.util.List;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;

public class ListFeatureExpanderPipe extends Pipe {
	
	@SuppressWarnings("unchecked")
	@Override
	public Instance pipe(Instance carrier){
		List<Feature> newFeats = new ArrayList<Feature>();
		
		for(Feature f : (List<Feature>)(carrier.getData())){
			if(!(f instanceof ListFeature)){
				newFeats.add(f);
				continue;
			}
				
			
			ListFeature lf = (ListFeature)f;
			
			for(Object o : lf.value()){
				newFeats.add(new NominalFeature(lf.id(), o));
			}
		}
		carrier.setData(newFeats);
		
		return carrier;
	}
	
	
	
	

}
