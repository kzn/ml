package name.kazennikov.ml.svm;

import java.util.Random;

public abstract class AbstractPegasosOVR {
	public static int ITER_PER_DOT = 1000000;
	protected int iter; 
	protected int k;
	protected double c;
	protected int start_iter;
		
	
	public AbstractPegasosOVR(int iter, int k, double c, int start_iter){
		this.iter = iter;
		this.k = k;
		this.c = c;
		this.start_iter = start_iter;
		
	}
	
	public abstract void init();
	public abstract int size();
	public abstract int numClasses();
	public abstract double target(int cls, int vec);
	public abstract double dot(int cls, int vec);
	public abstract void add(int cls, int vec, double factor);
	public abstract void scale(double factor);
	public abstract double snorm();
	
	
	
	// this code assumes that c is fixed for all samples
	// Doesn't use w.cost(i)
	public void solve() {
		init();

		int totdocs = size() * numClasses();
		double lambda = 1.0/c/totdocs;
		int size = size();

		Random gen = new Random();


		if(k == 0)
			k = totdocs;

		int[] xs = new int[k];
		int[] ys = new int[k];
		int ptr = 0; // index of A+ array




		for(int t = 1; t != iter; t++){
			ptr = 0;

			if(t % ITER_PER_DOT == 0){
				System.out.print(".");
				System.out.flush();
			}

			double eta = 1.0/(lambda * Math.sqrt(Math.sqrt(t + start_iter)));

			for(int i = 0; i != k; i++){
				int idx = Math.abs(gen.nextInt()) % totdocs;
				int cls = idx / size;
				idx = idx % size;

				double eval = target(cls, idx) * dot(cls, idx);
				if(eval - 1.0 < 10e-12) {  
					xs[ptr] = idx;
					ys[ptr] = cls;
					ptr++;
				}


			}

			scale(1 - 1.0/(t + start_iter + 1));

			for(int i = 0; i != ptr; i++){
				add(ys[i], xs[i], eta/k*target(ys[i], xs[i]));
			}


			double snorm = snorm();
			if(snorm > 1.0/lambda)
				scale(Math.sqrt(1.0/(lambda * snorm)));
		}
		System.out.println("Done.");
	}



}
