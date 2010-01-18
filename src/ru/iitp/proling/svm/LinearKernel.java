import ru.iitp.proling.svm.Kernel;


public class LinearKernel extends Kernel {

	@Override
	public double dot(SparseVector x, SparseVector y) {
		double res = 0;
		int j = 0;
		int k = 0;
		
		while(j != x.indexes.length	 && k != y.indexes.length){
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
		
		return res;

	}

	@Override
	public double dot(double[] v, SparseVector x) {
		double sum = 0;
		
		for(int i = 0; i != x.indexes.length; i++){
			sum += v[x.indexes[i]] * x.values[i];
		}
		return sum;
	}

}
