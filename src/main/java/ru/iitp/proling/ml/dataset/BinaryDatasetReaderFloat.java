package ru.iitp.proling.ml.dataset;


import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BinaryDatasetReaderFloat implements Closeable {
	DataInputStream s;
	
	int target;
	int qid;

	TIntArrayList indexes = new TIntArrayList();
	TFloatArrayList values = new TFloatArrayList();
	
	public BinaryDatasetReaderFloat(InputStream s) {
		if(s instanceof DataInputStream) {
			this.s = (DataInputStream) s;
		} else {
			this.s = new DataInputStream(s);
		}
	}

	@Override
	public void close() throws IOException {
		if(s != null)
			s.close();
	}
	
	public boolean hasNext() throws IOException {
		try {
			indexes.clear();
			values.clear();
			target = s.readInt();
			qid = s.readInt();
			int size = s.readInt();
			
			for(int i = 0; i != size; i++) {
				indexes.add(s.readInt());
				values.add(s.readFloat());
			}

			return true;
		} catch(EOFException e) {
			return false;
		}
	}
	
	public int target() {
		return target;
	}
	
	public int qid() {
		return qid;
	}
	
	public int[] indexes() {
		return indexes.toArray();
	}
	
	public float[] values() {
		return values.toArray();
	}
	

}
