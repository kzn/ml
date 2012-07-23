package ru.iitp.proling.feature;

/**
 * Generic high level feature type. Currently, there is 3 feature types:
 * 1. Numeric
 * 2. Nominal
 * 3. Set/List
 * @author ant
 *
 */
public abstract class Feature {
	int id;
	Object value;
	
	public Feature(int id, Object value){
		this.id = id;
		this.value = value;
	}
	
	public int id(){
		return id;
	}
	
	public Object value(){
		return value;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append('=');
		sb.append(value);
		return sb.toString();
	}

}
