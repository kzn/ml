package ru.iitp.proling.svm;
import gnu.trove.TObjectIntHashMap;
import java.util.ArrayList;

/**
 * Fast mapper between Object <-> int
 * @author ant
 *
 */
public class Alphabet<T> {
	protected TObjectIntHashMap<T> map;
	protected ArrayList<T> entries;
	
	public Alphabet(int capacity, T emptyObject){
		map = new TObjectIntHashMap<T>();
		entries =  new ArrayList<T>();	
		entries.add(emptyObject);
		map.put(emptyObject, 0);
	}
	
	public T get(int i){
		return entries.get(i);
	}
	
	public int get(T object, boolean add){
		if(object == null)
			throw new IllegalArgumentException ("Can't lookup \"null\" in an Alphabet.");
		
		int retIndex = -1;
		if(map.containsKey(object)) {
				retIndex = map.get(object);
			}
		else if (add) {
			retIndex = entries.size();
			map.put(object, retIndex);
			entries.add(object);
		}
		
		return retIndex;
	}
	
	public int get(T object){
		return get(object, true);
	}
	
	public int size(){
		return entries.size();
	}
}
