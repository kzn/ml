package name.kazennikov.ml.dataset;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple CSV Reader. Doesn't handle multiline records
 * 
 * @author Anton Kazennikov
 *
 */
public class CSVReader implements Closeable {
	BufferedReader reader;
	List<String> fields = new ArrayList<String>();
	
	public CSVReader(Reader r) {
		this.reader = new BufferedReader(r);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
	
	public boolean readNext() throws IOException {
		while(true) {
			String s = reader.readLine();
			
			if(s == null)
				return false;
			
			if(s.isEmpty())
				continue;
			

			parseFields(s, fields);
			return true;
		}
	}

	private void parseFields(String s, List<String> fields) {
		int pos = 0;
		fields.clear();
		
		while(pos < s.length()) {
			pos = skipWS(s, pos);
			char ch = s.charAt(pos);
			int end = ch == '"'? skipString(s, pos) : s.indexOf(',', pos);
			if(end == -1)
				end = s.length();
			fields.add(s.substring(pos, end).trim());
			pos = end + 1;
		}	
	}
	private int skipWS(String s, int pos) {
		while(pos < s.length() && Character.isWhitespace(s.charAt(pos)))
			pos++;
		return pos;
	}

	/**
	 * Skip string with possible quotation: "this \" is a quote"
	 * @param s
	 * @param pos
	 * @return
	 */
	private int skipString(String s, int pos) {
		char ch = s.charAt(pos);
		if(ch != '"')
			return pos;
		
		pos++;
		
		while(pos < s.length()) {
			ch = s.charAt(pos++);
			if(ch == '\\') {
				pos ++; // skip escaped char
				continue;
			}
			if(ch == '\"')
				break;
		}
		return pos;
	}
	
	public int fieldCount() {
		return fields.size();
	}
	
	public String getField(int index) {
		return fields.get(index);
	}
	
	

}
