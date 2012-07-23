package ru.iitp.proling.feature;

import java.util.Arrays;
import java.util.List;

import cc.mallet.types.Alphabet;

public class NamedListFeature extends NamedFeature {

	private static final long serialVersionUID = 1L;

	public NamedListFeature(String name, List<? extends Object> value) {
		super(name, Arrays.asList(value.toArray()));
	}
	
	public NamedListFeature(String name, Object...objects){
		super(name, Arrays.asList(objects));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> value(){
		return (List<Object>)value;
	}

	@Override
	ListFeature compile(Alphabet alphabet) {
		return new ListFeature(alphabet.lookupIndex(name), value()); 
	}
}
