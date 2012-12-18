package name.kazennikov.ml.dataset;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

public class SVMLightReader<E> implements Closeable {
	BufferedReader br;
	
	E label;
	double cost;
	int qid;
	TIntArrayList indexes = new TIntArrayList();
	TDoubleArrayList values = new TDoubleArrayList();
	LabelParser<E> labelParser;
	
	
	public SVMLightReader(Reader r, LabelParser<E> labelParser) {
		br = new BufferedReader(r);
		this.labelParser = labelParser;
	}
	
	@Override
	public void close() throws IOException {
		if(br != null)
			br.close();
	}
	
	public E label() {
		return label;
	}
	
	public boolean readNext() throws IOException {
		while(true) {
			String line = br.readLine();
			
			if(line == null)
				return false;
			
			line = line.trim();
			
			int end = line.indexOf('#');
			
			if(end != -1)
				line = line.substring(0, end);

			StringTokenizer st = new StringTokenizer(line, " ");
			if(!st.hasMoreTokens())
				continue;
			
			// parse targets			
			label = labelParser.parse(st.nextToken());
			
            

            TIntArrayList indexes = new TIntArrayList();
            TDoubleArrayList values = new TDoubleArrayList();
            
            indexes.clear();
            values.clear();
            
            while(st.hasMoreTokens()) {
            	String pair = st.nextToken();
            	int sepIndex = pair.indexOf(':');
            	String first = pair.substring(0, sepIndex);
            	String value = pair.substring(sepIndex + 1, pair.length());
            	
            	switch(first.charAt(0)){
            	case 'q':
            		qid = Integer.parseInt(value);
            		break;
            	case 'c':
            		cost = Double.parseDouble(value);
            		break;
            	default:
            		indexes.add(Integer.parseInt(first));
            		values.add(Double.parseDouble(value));
            	}
            }
			
		}
	}

}
