package ru.iitp.proling.feature;

import java.io.Serializable;

import cc.mallet.types.Alphabet;

public abstract class NamedFeature implements Serializable{
	private static final long serialVersionUID = 1L;
	final String name;
	final Object value;
	
	public NamedFeature(String name, Object value){
		this.name = name;
		this.value = value;
	}
	
	public String id(){
		return name;
	}
	
	public Object value(){
		return value;
	}
	
	abstract Feature compile(Alphabet alphabet);

}
