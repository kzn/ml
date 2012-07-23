package ru.iitp.proling.feature;

/**
 * Nominal feature. Used for enumeration values such as colors, or a set of string.
 * The distinction between nominal and ListFeature is that Nominal feature can take
 * only one value from the set, whereas ListFeature can take several possibly repeated values
 * as for example color={black, white}. A nominal feature can be only color={black}, color={white}
 * @author ant
 *
 */
public class NominalFeature extends Feature {

	public NominalFeature(int id, Object value) {
		super(id, value);
	}
}
