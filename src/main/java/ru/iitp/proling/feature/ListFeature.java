package ru.iitp.proling.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListFeature extends Feature {

	public ListFeature(int id, List<? extends Object> value){
		super(id, Arrays.asList(value.toArray()));
	}
	
	public ListFeature(int id, Object... values){
		super(id, Arrays.asList(values));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> value(){
		return (List<Object>)value;
	}
	
	public void addValue(Object value){
		value().add(value);
	}

}
