/**
 * 
 */
package ru.iitp.proling.feature;


/**
 * Pair for {id, value} -> int mapping 
 * @author ant
 *
 */
class ComplexPair{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComplexPair other = (ComplexPair) obj;
		if (index != other.index)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	int index;
	Object value;
	public ComplexPair(int index, Object value){
		this.index = index;
		this.value = value;
	}
	
	
}