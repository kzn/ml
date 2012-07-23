import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import ru.iitp.proling.svm.BasicDataset;


public class CompileDataset {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if(args.length != 2){
			System.out.println("Tool to compile from symbolic dataset to java binary serialization form");
			System.out.println("Usage: program dataset ser.out");
			return;
		}
		
		BasicDataset dset = BasicDataset.readText(args[0]);
		
		FileOutputStream fos = new FileOutputStream(args[1]);
		OutputStream os = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		
		oos.writeObject(dset);
		oos.close();
		os.close();
		fos.close();
		
	}

}
