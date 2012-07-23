package ru.iitp.proling.ml.dataset;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BinaryModels {
	public static double[] readVector(InputStream s) throws IOException {
		DataInputStream dis = new DataInputStream(s);
		int size = dis.readInt();
		double[] w = new double[size];
		for(int i = 0; i != size; i++) {
			w[i] = dis.readDouble();
		}
		return w;
	}
	
	public static void writeVector(OutputStream s, double[] w) throws IOException {
		DataOutputStream dos = new DataOutputStream(s);
		dos.writeInt(w.length);
		for(int i = 0; i != w.length; i++) {
			dos.writeDouble(w[i]);
		}
	}

}
