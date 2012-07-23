package ru.iitp.proling.feature;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.plaf.basic.BasicTreeUI.TreeHomeAction;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;

/**
 * Simple Encoder for Numeric/Nominal 
 * 1.  
 * 
 * @author ant
 *
 */
public class SimpleEncoderPipe extends Pipe {

	private static final long serialVersionUID = 1L;
	Alphabet feats;
	
	public SimpleEncoderPipe(Alphabet feats){
		this.feats = feats;
	}
	
	
	@SuppressWarnings("unchecked")
	public Instance pipe(Instance carrier){
		SortedMap<Integer, Double> hm = new TreeMap<Integer, Double>();
		
		List<Feature> feats = (List<Feature>)carrier.getData();
		for(Feature f : feats){
			if(f instanceof NumericFeature)
				hm.put(getIndex(f.id()), ((Double)f.value()));
			
			if(f instanceof NominalFeature)
				hm.put(getIndex(f.id()), ((Integer)f.value()).doubleValue());
			
		}

		int[] indexes = new int[hm.size()];
		double[] values = new double[hm.size()];
		int i = 0;
		for(Entry<Integer, Double> entry : hm.entrySet()){
			indexes[i] = entry.getKey();
			values[i] = entry.getValue();
			i++;
		}
		FeatureVector fv = new FeatureVector(this.feats, indexes, values);
		carrier.setData(fv);
		return carrier;
	}
	
	
	int getIndex(int id, int valueId){
		long key = id;
		key <<= 32;
		key += valueId;
		return feats.lookupIndex(Long.valueOf(key));
	}
	
	int getIndex(int id){
		return feats.lookupIndex((long)id);
	}
	
	public int size(){
		return feats.size();
	}

}
