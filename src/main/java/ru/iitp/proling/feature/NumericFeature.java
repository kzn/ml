package ru.iitp.proling.feature;

/**
 * Simple numeric feature. Used for numeric values such as length, speed, weight etc.
 * @author ant
 *
 */
public class NumericFeature extends Feature {

	public NumericFeature(int id, Double value) {
		super(id, value);
	}
	
	@Override
	public Double value(){
		return (Double)value;
	}

}
