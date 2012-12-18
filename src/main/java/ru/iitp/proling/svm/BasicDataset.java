package ru.iitp.proling.svm;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import name.kazennikov.common.Alphabet;
import name.kazennikov.ml.core.Instance;
import name.kazennikov.ml.core.NativeInstance;
import ru.iitp.proling.ml.core.Dataset;


public class BasicDataset implements Dataset<Double>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int dim = 0;
	protected List<RWSample<Double>> samples = new ArrayList<RWSample<Double>>();
	protected Alphabet<Double> alphabet = new Alphabet<Double>();
	protected TIntArrayList targets = new TIntArrayList();
	
	
	public BasicDataset(){
	}

	/**
	 * Add new sample to the dataset
	 * @param sample sample to add
	 * @return index of the sample in the dataset
	 */
	public int add(RWSample<Double> sample){
		targets.add(alphabet.get(sample.value()));
		samples.add(sample);
		dim = Math.max(dim, sample.dim());
		return size() - 1;
	}

	@Override
	public int dim() {
		return dim;
	}

	public List<RWSample<Double>> instances() {
		return samples;
	}
	

	@Override
	public int size() {
		return samples.size();
	}

	@Override
	public RWSample<Double> get(int idx) {
		return samples.get(idx);
	}
	
	@Override
	public Alphabet<Double> alphabet(){
		return alphabet;
	}
	
	public int[] targets(){
		return targets.toArray();
	}
	
	public double target(int i){
		return alphabet.get(targets.get(i));
	}
	
	private void writeObject(ObjectOutputStream s) throws IOException{
		s.writeObject(samples);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException{
		samples = (List<RWSample<Double>>)s.readObject();
		targets = new TIntArrayList(samples.size());
		alphabet = new Alphabet<Double>();
		for(int i = 0; i != samples.size(); i++){
			targets.add(alphabet.get(samples.get(i).value()));
			dim = Math.max(dim, samples.get(i).dim());
		}
		
	}
	
	public static BasicDataset readBinary(String filename) throws IOException, ClassNotFoundException{
		BasicDataset dataset;
		FileInputStream fis = new FileInputStream(filename);
		GZIPInputStream is = new GZIPInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(is);
		dataset = (BasicDataset)ois.readObject();
		ois.close();
		is.close();
		fis.close();
		
		return dataset;
	}
	
	public void writeBinary(String filename) throws IOException{
		FileOutputStream fos = new FileOutputStream(filename);
		GZIPOutputStream os = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(this);
		oos.close();
		os.close();
		fos.close();
	}
	
	public static BasicDataset readText(String filename) throws IOException {
		BasicDataset dataset = new BasicDataset();
		
		System.out.println("Reading:" + filename);
		
		InputStream is = new FileInputStream(filename);
		if(filename.endsWith(".gz"))
			is = new GZIPInputStream(is);
		BufferedReader fp = new BufferedReader(new InputStreamReader(is));
		
		
		int n = 0;
		while(true){
			String line = fp.readLine();
			if(line == null)
				break;
			int end = line.indexOf('#');
			
			if(end != -1)
				line = line.substring(0, end);
			

			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
			if(!st.hasMoreTokens())
				continue;
            String token = st.nextToken();
            n++;
            
            if(n % 10000 == 0){
            	System.out.printf("%d..", n);
            	System.out.flush();
            }
            // parse target
            double target = Double.parseDouble(token);
            TIntArrayList indexes = new TIntArrayList();
            TDoubleArrayList values = new TDoubleArrayList();
        	double cost = 1.0;
        	int qid = 0;

            while(st.hasMoreTokens()){
            	String first = st.nextToken();
            	String value = st.nextToken();
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
            dataset.add(new RWSample<Double>(target, indexes.toArray(),values.toArray(), cost, qid));
            
            
		}

/*		StreamTokenizer ft = new StreamTokenizer(fp);
		ft.commentChar('#');
		ft.ordinaryChar(':');
		ft.eolIsSignificant(true);



		while(true){
			n++;

			if(n % 10000 == 0){
				System.out.print(Integer.toString(n) + "..");
				System.out.flush();

			}

			int ttype = ft.nextToken(); // token type

			if(ttype == StreamTokenizer.TT_EOF | ttype == StreamTokenizer.TT_EOL)
				break;


			assert(ttype == StreamTokenizer.TT_NUMBER);
			double target = ft.nval;

			List<Integer> idxs = new ArrayList<Integer>();
			List<Double> vals = new ArrayList<Double>();
			int qid = 0;
			double cost = 1.0;


			while(true){
				ttype = ft.nextToken();
				if(ttype == StreamTokenizer.TT_EOF | ttype == StreamTokenizer.TT_EOL)
					break;



				int idx = 0;
				if(ttype == StreamTokenizer.TT_NUMBER)
					idx = (int)ft.nval;


				int idx_type = ttype;
				String field = null;
				if(ft.sval != null)
					field = ft.sval.intern();


				ttype = ft.nextToken();

				assert(ttype == ':');

				ttype = ft.nextToken();
				assert(ttype == StreamTokenizer.TT_NUMBER);

				if(idx_type == StreamTokenizer.TT_NUMBER){
					idxs.add(idx);
					vals.add(ft.nval);
				}else{
					char c = field.charAt(0);

					switch(c){
					case 'q':
						qid = (int) ft.nval;
						break;
					case 'c':
						cost = ft.nval;
						break;
					}


				}



			}

			dataset.add(new RWSample<Double>(target, idxs, vals, cost, qid));
		}*/

		System.out.print(Integer.toString(n - 1) + ".Done!\n");
		is.close();
		return dataset;
	}

	
}