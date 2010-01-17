import java.util.ArrayList;
import java.util.List;


public class WeightVectorRanking implements WeightVector {
	protected WeightVectorLinear w;
	protected Dataset dataset;
	protected double[] alphas;
	protected double[] targets;
	protected double[] sqnorms;
	
	// actual vector is a - b
	protected int[] a;
	protected int[] b;
	
	
	
	public WeightVectorRanking(Dataset ds){
		dataset = ds;
		w = new WeightVectorLinear(dataset);
		List<Integer> _a = new ArrayList<Integer>();
		List<Integer> _b = new ArrayList<Integer>();
		List<Double> _targets = new ArrayList<Double>();
		
		
		
		
		for(int i = 0; i != dataset.size(); i++){ // a idx
			for(int j = i; j != dataset.size(); j++){ // b idx
				
				RWSample x = dataset.vec(i);
				RWSample y = dataset.vec(j);
				
				if(x.qid != y.qid)
					break;
				  
				if(x.target > y.target){ // pair a > b
					_a.add(i);
					_b.add(j);
					_targets.add(1.0 + x.weight - y.weight);
				}else if(x.target < y.target){ // pair b > a
					_a.add(j);
					_b.add(i);
					_targets.add(1.0 + y.weight - x.weight);
				}
			}
		}
		a = new int[_a.size()];
		b = new int[_b.size()];
		targets = new double[_targets.size()];
		
		for(int i = 0; i != _a.size(); i++){
			a[i] = _a.get(i);
			b[i] = _b.get(i);
			targets[i] = _targets.get(i);
			
		}
		
		System.out.printf("Constructed %d ranks\n", a.length);
		
		sqnorms = new double[a.length];
		
		for(int i = 0; i != a.length; i++){
			sqnorms[i] = dataset.snorm(a[i]) + dataset.snorm(b[i]);
			
			/// sparse dot code
			double res = 0;
			int j = 0;
			int k = 0;
			RWSample x = dataset.vec(a[i]);
			RWSample y = dataset.vec(b[i]);
			
			while(j != x.size	 && k != y.size){
				if(x.indexes[j] < y.indexes[k])
					j++;
				else if(x.indexes[j] > y.indexes[k])
					k++;
				else{
					res += x.values[j] * y.values[k];
					j++;
					k++;
				}
			}
			
			sqnorms[i] -= 2 * res;
		}
				
		alphas = new double[a.length];
	}

	@Override
	public void add(int idx, double factor) {
		w.add(a[idx], factor);
		w.add(b[idx], -factor);
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
		return w.dim();
	}

	@Override
	public double dot(int idx) {
		return w.dot(a[idx]) - w.dot(b[idx]);
	}

	@Override
	public double norm() {
		return w.norm();
	}

	@Override
	public int size() {
		return a.length;
	}

	@Override
	public double snorm() {
		return w.snorm();
	}

	@Override
	public double snorm(int idx) {
		return sqnorms[idx];
	}

	@Override
	public double target(int idx) {
		return targets[idx];
	}

}
