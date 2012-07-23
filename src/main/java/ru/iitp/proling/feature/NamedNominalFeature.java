package ru.iitp.proling.feature;

import cc.mallet.types.Alphabet;

public class NamedNominalFeature extends NamedFeature {
	private static final long serialVersionUID = 1L;	

	public NamedNominalFeature(String name, Object value) {
		super(name, value);
	}
	
	@Override
	NominalFeature compile(Alphabet alphabet) {
		return new NominalFeature(alphabet.lookupIndex(name), value()); 
	}

}
