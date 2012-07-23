package ru.iitp.proling.feature;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TObjectIntHashMap;
import cc.mallet.types.SparseVector;
import ru.iitp.proling.common.Alphabet;
/**
 * Initial class to transforming feature to binary features
 * @author ant
 *
 */
public class BinaryFeatureVectorBuilder {
	TIntArrayList indexes = new TIntArrayList();
	TObjectIntHashMap<String> alphabet;
	
	
	public BinaryFeatureVectorBuilder(TObjectIntHashMap<String> alphabet){
		this.alphabet = alphabet;
	}
	
	public void add(Object... keys){
		StringBuilder sb = new StringBuilder();
		for(Object o : keys){
			sb.append(o.toString());
		}
		add(lookupIndex(sb.toString()));
	}
	
	public void add(String string){
		indexes.add(lookupIndex(string));
	}

	int lookupIndex(String string){
		//String string = sb.toString();
		int index = alphabet.get(string);
		if(index == 0){
			index = alphabet.size() + 1;
			alphabet.put(string, index);
		}

		return index;
	}
	
	public SparseVector build(){
		return new SparseVector(indexes.toArray(), false);
	}

}
