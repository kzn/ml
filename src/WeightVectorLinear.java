
public class WeightVectorLinear implements WeightVector {
	protected Dataset dataset;
	protected double[] alphas;
	protected double[] v;
	
	public WeightVectorLinear(Dataset ds){
		dataset = ds;
		alphas = new double[ds.size()];
		v = new double[dataset.max_dim() + 1];
	}

	@Override
	public void add(int idx, double factor) {
		RWSample s = dataset.vec(idx);
		
		for(int i = 0; i != s.size; i++){
			v[s.indexes[i]] += s.values[i] * factor;
		}
	}

	@Override
	public void add_alpha(int idx, double factor) {
		alphas[idx] += factor;
	}

	@Override
	public double alpha(int idx) {
		return alphas[idx];
	}

	@Override
	public int dim() {
		return dataset.max_dim();
	}

	@Override
	public double dot(int idx) {
		RWSample s = dataset.vec(idx);
		double sum = 0;
		
		for(int i = 0; i != s.size; i++){
			sum += v[s.indexes[i]] * s.values[i];
		}
		return sum;
	}

	@Override
	public int size() {
		return dataset.size();
	}

	@Override
	public double norm() {
		return Math.sqrt(snorm());
	}
	
	@Override
	public double snorm(){
		double n = 0;
		
		for(int i = 0; i != dim(); i++)
			n += v[i] * v[i];
		
		return n;
	
	}

	@Override
	public double snorm(int idx) {
		return dataset.snorm(idx);
	}

	@Override
	public double target(int idx) {
		return dataset.vec(idx).target;
	}

}
