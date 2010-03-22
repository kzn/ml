package ru.iitp.proling.svm;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import ru.iitp.proling.common.Alphabet;


public class BasicDataset implements Dataset<Double> {
	protected int dim;
	protected List<RWSample> samples;
	protected Alphabet<Double> alphabet;
	protected int[] targets;
	
	public BasicDataset(String filename){
		dim = 0;
		alphabet = new Alphabet<Double>(0.0);
		samples = new ArrayList<RWSample>();
		
		this.read(filename);
	}

	@Override
	public int dim() {
		return dim;
	}

	
	public void read(String filename) {
		System.out.println("Reading:" + filename);

		try{
			BufferedReader fp = new BufferedReader(new FileReader(filename));

			StreamTokenizer ft = new StreamTokenizer(fp);
			ft.commentChar('#');
			ft.ordinaryChar(':');
			ft.eolIsSignificant(true);
			
			int n = 0;
			List<Integer> t = new ArrayList<Integer>();



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
				t.add(alphabet.get(target));
				

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
				
				samples.add(new RWSample(target, idxs, vals, cost, qid));
				dim = Math.max(dim, idxs.get(idxs.size()-1));
			}
			
			targets = new int[t.size()];
			for(int i = 0; i != t.size(); i++)
				targets[i] = t.get(i);
			
			System.out.print(Integer.toString(n - 1) + ".Done!\n");
		}catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public int size() {
		return samples.size();
	}

	@Override
	public RWSample get(int idx) {
		return samples.get(idx);
	}

	@Override
	public int[] qids() {
		int qids[] = new int[size()];
		for(int i = 0; i != size(); i++)
			qids[i] = get(i).qid;
		return qids;
	}

	@Override
	public int[] targets() {
		return targets;
	}

	@Override
	public Alphabet<Double> alphabet() {
		return alphabet;
	}

	@Override
	public int qid(int idx) {
		// TODO Auto-generated method stub
		return samples.get(idx).qid;
	}

	@Override
	public int target(int idx) {
		// TODO Auto-generated method stub
		return targets[idx];
	}
	
	public double[] labels(){
		double[] labels = new double[size()];
		
		for(int i = 0; i != labels.length; i++)
			labels[i] = alphabet.get(targets[i]);
		
		return labels;
		
	}

}