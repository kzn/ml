import java.util.List;

public class Sample extends SparseVector {
	public double target;
	
	
	Sample(double target, List<Integer> idxs, List<Double> vals){
		super(idxs, vals);
		this.target = target;
	}
}
