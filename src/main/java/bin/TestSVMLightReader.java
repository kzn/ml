package bin;

import java.io.FileReader;
import java.io.IOException;

import name.kazennikov.ml.dataset.SVMLightReaderInteger;

public class TestSVMLightReader {
	public static void main(String[] args) throws IOException {
		SVMLightReaderInteger r = new SVMLightReaderInteger(new FileReader("train.dat"));
		
		int count = 0;
		while(r.readNext()) {
			count++;
		}
		
		
		System.out.printf("Read %d samples%n", count);
		r.close();
	}

}
