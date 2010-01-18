package ru.iitp.proling.svm;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;


public class BasicDataset implements Dataset {
	protected int dim;
	protected List<Double> sqnorms;
	protected List<RWSample> samples;
	
	public BasicDataset(String filename){
		dim = 0;
		sqnorms = new ArrayList<Double>();
		samples = new ArrayList<RWSample>();
		this.read(filename);
	}

	@Override
	public int max_dim() {
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
				double norm = 0.0;


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
						norm += ft.nval * ft.nval;
						
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
				sqnorms.add(norm);
				dim = Math.max(dim, idxs.get(idxs.size()-1));
			}
			
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
	public double snorm(int idx) {
		// TODO Auto-generated method stub
		return sqnorms.get(idx);
	}

	@Override
	public RWSample vec(int idx) {
		// TODO Auto-generated method stub
		return samples.get(idx);
	}

}