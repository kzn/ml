package ru.iitp.proling.feature;

import cc.mallet.types.Alphabet;

public class NamedNumericFeature extends NamedFeature {
	private static final long serialVersionUID = 1L;

	public NamedNumericFeature(String name, Double value) {
		super(name, value);
	}
	
	@Override
	public Double value(){
		return (Double)value;
	}
	
	@Override
	NumericFeature compile(Alphabet alphabet) {
		return new NumericFeature(alphabet.lookupIndex(name), value()); 
	}

}
