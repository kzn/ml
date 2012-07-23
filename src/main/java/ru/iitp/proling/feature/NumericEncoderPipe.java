package ru.iitp.proling.feature;


import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.SparseVector;

/**
 * Encode list of numeric/nominal features to numeric feature vector
 * @author ant
 *
 */
public class NumericEncoderPipe extends Pipe{

	private static final long serialVersionUID = 1L;
	TLongIntHashMap featsN = new TLongIntHashMap();
	Alphabet feats;
	int nNumericFeats = 0;
	
	public NumericEncoderPipe(Alphabet feats){
		this.feats = feats;

	}
	
	
	@SuppressWarnings("unchecked")
	public Instance pipe(Instance carrier){
		/*SortedMap<Integer, Double> hm = new TreeMap<Integer, Double>();
		
		List<Feature> feats = (List<Feature>)carrier.getData();
		for(Feature f : feats){
			if(f instanceof NumericFeature)
				hm.put(getIndex(f.id()), ((Double)f.value()));
			
			if(f instanceof NominalFeature){
				hm.put(getIndex(f.id(), (Integer)f.value()), 1.0);
			}
			
		}
		
		
		int[] indexes = new int[hm.size()];
		double[] values = new double[hm.size()];
		int i = 0;
		for(Entry<Integer, Double> entry : hm.entrySet()){
			indexes[i] = entry.getKey();
			values[i] = entry.getValue();
			i++;
		}
		FeatureVector fv = new FeatureVector(this.feats, indexes, values);*/
		TIntArrayList indexes = new TIntArrayList();
		TDoubleArrayList values = new TDoubleArrayList();
		List<Feature> feats = (List<Feature>)carrier.getData();
		for(Feature f : feats){
			if(f instanceof NumericFeature){
				indexes.add(getIndex(f.id()));
				values.add((Double)f.value());
			}
			
			if(f instanceof NominalFeature){
				indexes.add(getIndex(f.id(), (Integer)f.value()));
				values.add(1.0);
			}
			
		}
		
		
		carrier.setData(new SparseVector(indexes.toArray(), values.toArray(), false, true));
		return carrier;
	}
	
	int getIndex(int id, int valueId){
		long key = id;
		key <<= 32;
		key += valueId;
		return getIndex(key);//feats.lookupIndex(Long.valueOf(key));
	}
	
	int getIndex(long id){
		//return feats.lookupIndex((long)id);
		int val = featsN.get(id);
		if(val != 0)
			return val;
		
		val = featsN.size() + 1;
		featsN.put(id, val);
		return val;
		
	}

	public int size(){
		return feats.size();
	}

}
