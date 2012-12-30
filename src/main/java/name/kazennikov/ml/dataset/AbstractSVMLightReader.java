package name.kazennikov.ml.dataset;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

/**
 * Reader for SVM-Light sample format.
 * <p><p>
 * The format is label + instance sparse vector: <p>
 * [label] cost:[value] qid:[value] [index_1]:[value_1] [index_2]:[value_2] ... [index_n]:[value_n] \n
 * <p><p>
 * For example: 1 cost:5.0 qid:3 1:0.5 5:0.02 7:-4
 * <p><p>
 * This class should be extended with custom label parser. This is needed as the label
 * could be a single real number (for binary classification 1.0/-1.0, or regression), integer number (for
 * multiclass problem or ordinal regression), or set of numbers (for multicategory classification).
 * 
 * <p><p>
 * The sparse vector is encoded by non-zero coordinate values. There are also special sample properties:<ul>
 * <li> <b>cost</b> - the importance of this sample
 * <li> <b>qid</b> - query id of the sample (needed for ordinal regression, where list or samples with same qid are 
 * grouped in one logical training sample)
 * 
 * @author kzn
 *
 */
public abstract class AbstractSVMLightReader implements Closeable {
	BufferedReader br;
	
	double cost;
	int qid;
	TIntArrayList indexes = new TIntArrayList();
	TDoubleArrayList values = new TDoubleArrayList();
	
	public AbstractSVMLightReader(Reader r) {
		br = new BufferedReader(r);

	}
	
	@Override
	public void close() throws IOException {
		if(br != null)
			br.close();
	}
	
	/**
	 * Read next sample.
	 * @return true if sample is read successfully
	 * @throws IOException
	 */
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
			parseLabel(st.nextToken());

            
            indexes.clear();
            values.clear();

            // parse values
            while(st.hasMoreTokens()) {
            	String pair = st.nextToken();
            	int sepIndex = pair.indexOf(':');
            	String first = pair.substring(0, sepIndex);
            	String value = pair.substring(sepIndex + 1, pair.length());
            	
            	switch(first.charAt(0)) {
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
            
            return true;
		}
	}

	/**
	 * Parse label token
	 * @param nextToken
	 */
	protected abstract void parseLabel(String nextToken);
	
	public int[] indexes() {
		return indexes.toArray();
	}
	
	public double[] values() {
		return values.toArray();
	}
}
