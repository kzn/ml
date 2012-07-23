package ru.iitp.proling.ml.mallet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class InstanceListUtils {
	
	public static InstanceList readInstanceStream(String filename) throws IOException, ClassNotFoundException{
		List<Instance> instances = new ArrayList<Instance>();
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Pipe p;
		
		while(true){
			Object o = ois.readObject();
			if(o instanceof Pipe){
				p = (Pipe)o;
				break;
			}
			instances.add((Instance)o);
		}
		InstanceList il = new InstanceList(p);
		for(Instance i : instances)
			il.add(i);
		
		return il;
	}
	
	public static void writeInstanceStream(String filename, InstanceList il) throws IOException{
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		for(Instance i : il){
			oos.writeObject(i);
		}
		
		oos.writeObject(il.getPipe());
		
		oos.close();
		fos.close();
		
	}

}
