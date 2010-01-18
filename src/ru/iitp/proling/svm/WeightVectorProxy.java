
public class WeightVectorProxy extends WeightVector {
	protected WeightVector w;
	
	public WeightVectorProxy(WeightVector base){
		w = base;
	}

	@Override
	public void add(int idx, double factor) {
		w.add(idx, factor);
	}

	@Override
	public void add_alpha(int idx, double factor) {
		w.add_alpha(idx, factor);
	}

	@Override
	public double alpha(int idx) {
		return w.alpha(idx);
	}

	@Override
	public int dim() {
		return w.dim();
	}

	@Override
	public double dot(int idx) {
		return w.dot(idx);
	}

	@Override
	public double dot(int x, int y) {
		return w.dot(x,y);
	}

	@Override
	public double norm() {
		return w.norm();
	}

	@Override
	public int size() {
		return w.size();
	}

	@Override
	public double snorm() {
		return w.snorm();
	}

	@Override
	public double snorm(int idx) {
		return w.snorm(idx);
	}

	@Override
	public double target(int idx) {
		return w.target(idx);
	}

}
