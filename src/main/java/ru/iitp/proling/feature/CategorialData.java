package ru.iitp.proling.feature;


import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategorialData implements Serializable{
	TObjectIntHashMap<Object> keyAlphabet = new TObjectIntHashMap<Object>();
	List<TObjectIntHashMap<Object>> valueAlphabet = new ArrayList<TObjectIntHashMap<Object>>();
	
	
	
	public int lookupKey(Object key){
		int keyIndex = keyAlphabet.get(key);
		if(keyIndex == 0){
			keyIndex = keyAlphabet.size() + 1;
			keyAlphabet.put(key, keyIndex);
			valueAlphabet.add(new TObjectIntHashMap<Object>());
		}
		
		return keyIndex;
		
	}
	public int lookupValue(int key, Object value){
		TObjectIntHashMap<Object> a = valueAlphabet.get(key - 1);
		int val = a.get(value);
		if(val == 0){
			val = a.size() + 1;
			a.put(value, val);
		}
		return val;
	}
	
	public void printStats(){
		Object[] keys = keyAlphabet.keys();
		int totVals = 0;
		for(Object key : keys){
			int index = keyAlphabet.get(key);
			totVals += valueAlphabet.get(index - 1).size();
			System.out.printf("%s(%d) -> %d%n", key, index, valueAlphabet.get(index - 1).size());
		}
		System.out.printf("Total values:%d%n", totVals);
	}
}
