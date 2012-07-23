package ru.iitp.proling.ml.dataset;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ru.iitp.proling.ml.core.Instance;

public class BinaryDatasetWriterFloat implements Closeable {
	DataOutputStream s;
	
	public BinaryDatasetWriterFloat(OutputStream s) {
		if(s instanceof DataOutputStream) {
			this.s = (DataOutputStream)s;
		} else {
			this.s = new DataOutputStream(s);
		}
	}
	
	public void writeInstance(int target, int qid, Instance instance) throws IOException {
		s.writeInt(target);
		s.writeInt(qid);
		s.writeInt(instance.size());

		for(int i = 0; i != instance.size(); i++) {
			s.writeInt(instance.indexAt(i));
			s.writeFloat((float)instance.valueAt(i));
		}
	}

	@Override
	public void close() throws IOException {
		if(s != null)
			s.close();
	}

}
