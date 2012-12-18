package bin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import name.kazennikov.ml.core.Instance;
import name.kazennikov.ml.dataset.BinaryDatasetReaderDouble;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import name.kazennikov.common.MurmurHash;
import ru.iitp.proling.ml.core.MalletInstance;
import ru.iitp.proling.svm.RWSample;

import cc.mallet.types.SparseVector;



public class PrintDataset {
	

	public static void main(String[] args) throws IOException {
		BasicConfigurator.configure();
		PropertyConfigurator.configure("log4j.properties");
		OptionParser op = new OptionParser();
		op.accepts("help", "Show help");

		OptionSet optSet = op.parse(args);
		
		if(args.length < 2) {
			System.out.printf("Usage: [in] [out]");
			System.exit(0);
		}
		
		BinaryDatasetReaderDouble dsReader = 
				new BinaryDatasetReaderDouble(Utils.openReadFile(args[0]));
		PrintWriter pw = new PrintWriter(args[1]);
		int count = 0;
		
		while(dsReader.hasNext()) {
			if(++count % 10000 == 0) {
				System.out.printf("%d..%n", count);
				System.out.flush();
			}
			int target = dsReader.target();
			int qid = dsReader.qid();
			int[] indexes = dsReader.indexes();
			double[] values = dsReader.values();
			pw.print(target);
			
			for(int i = 0; i != indexes.length; i++) {
				indexes[i] = (MurmurHash.hash(indexes[i], 0xcafebabe) >>> 1) % (1 << 22);
			}
			SparseVector sv = new SparseVector(indexes, values, false, true);
			Instance inst = new MalletInstance(sv);
			
			for(int i = 0; i != inst.size(); i++) {
				pw.print(' ');
				pw.print(inst.indexAt(i));
				pw.print(':');
				pw.print(inst.valueAt(i));
			}
			pw.println();


		}
		dsReader.close();



	}

}
