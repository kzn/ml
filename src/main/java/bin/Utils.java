package bin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Utils {

	public static OutputStream openWriteFile(String s) throws IOException {
		OutputStream stream = new FileOutputStream(s);
		if(s.endsWith(".gz"))
			stream = new GZIPOutputStream(stream);
		return stream;
	}
	
	public static InputStream openReadFile(String s) throws IOException {
		InputStream stream = new FileInputStream(s);
		if(s.endsWith(".gz"))
			stream = new GZIPInputStream(stream);
		
		return stream;
	}

}
