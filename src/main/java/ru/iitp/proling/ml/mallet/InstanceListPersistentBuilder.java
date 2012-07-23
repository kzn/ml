package ru.iitp.proling.ml.mallet;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Target2Label;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.SparseVector;

/**
 * Class for building an instancelist in two OOS streams backed by files
 * @author ant
 *
 */
public class InstanceListPersistentBuilder {
	String filename;
	OutputStream os;
	ObjectOutputStream oos;
	Alphabet dataAlphabet;
	Alphabet labelAlphabet;
	
	/**
	 * Init InstanceListPersistentBuilder from:
	 * @param filename base filename
	 * @throws IOException
	 */
	public InstanceListPersistentBuilder(String filename) throws IOException{
		this.filename = filename;
		os = new GZIPOutputStream(new FileOutputStream(filename));
		oos = new ObjectOutputStream(os);
	}
	
	/**
	 * Add new instance to this builder
	 * @param inst instance to add
	 * @return true, if added successfully
	 * @throws IOException
	 */
	public boolean addInstance(Instance inst) throws IOException{
		if(dataAlphabet == null)
			dataAlphabet = inst.getDataAlphabet();
		if(labelAlphabet == null)
			labelAlphabet = inst.getTargetAlphabet();
		if(dataAlphabet != inst.getDataAlphabet() || labelAlphabet != inst.getTargetAlphabet())
			return false;
		
		oos.writeObject(inst);
		return true;
	}
	
	/**
	 * Finalize the builder and close all underlying streams
	 * @throws IOException
	 */
	public void close() throws IOException{
		oos.close();
		os.close();
		
		FileOutputStream alphabetOS = new FileOutputStream(filename + ".alpha");
		ObjectOutputStream alphabetOOS = new ObjectOutputStream(alphabetOS);
		alphabetOOS.writeObject(dataAlphabet);
		alphabetOOS.writeObject(labelAlphabet);
		alphabetOOS.close();
		alphabetOS.close();
	}
	

	/**
	 * Read InstanceList from files 
	 * @param filename base filename
	 * @return fresh InstanceList
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static InstanceList read(String filename, int maxSamples) throws IOException, ClassNotFoundException{
		InputStream isAlpha = new FileInputStream(filename + ".alpha");
		ObjectInputStream oisAlpha = new ObjectInputStream(isAlpha);
		Alphabet data = (Alphabet)oisAlpha.readObject();
		//Alphabet targets = (Alphabet)oisAlpha.readObject();
		oisAlpha.close();
		isAlpha.close();
		
		
		
		InstanceList il = new InstanceList(new Target2Label(null, new LabelAlphabet()));
		InputStream is = new GZIPInputStream(new FileInputStream(filename));
		ObjectInputStream ois = new ObjectInputStream(is);
		int i = 0;
		try{
			while(maxSamples == 0 || i < maxSamples){
				Instance instance = (Instance)ois.readObject();
				SparseVector sv = (SparseVector)instance.getData();
				SparseVector sv1 = new SparseVector(sv.getIndices(), null, false, false);
				instance.setData(sv1);
				//FeatureVector fv = new FeatureVector(data, sv.getIndices(), sv.getValues());
				//instance.setData(fv);
				il.addThruPipe(instance);
				i++;
				if(i % 10000 == 0){
					System.out.printf("%d..", i);
					System.out.flush();
				}
			}
		}catch(EOFException e){
		}
		ois.close();
		is.close();
		
		return il;
	}
	
	public static void writeOut(InstanceList il, String filename) throws IOException{
		BufferedWriter fp = new BufferedWriter(new FileWriter(filename));
		
		
		for(Instance inst : il){
			SparseVector sv = (SparseVector)inst.getData();
			Object target = inst.getTarget();
			
			fp.write(target.toString());

			for(int i = 0; i != sv.numLocations(); i++){
				fp.append(' ');
				fp.write(Integer.toString(sv.indexAtLocation(i)));
				fp.append(':');
				fp.write(Double.toString(sv.valueAtLocation(i)));
			}
			fp.append('\n');
		}
		fp.close();
	}
	
	

}
