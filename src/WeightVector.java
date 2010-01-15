
public interface WeightVector {
	
	// direct wv properties
	int dim(); // dimension of model
	double norm();
	double snorm();
	
	
	
	// problem properties
	int size(); // problem size
	void add(int idx, double factor);
	
	void add_alpha(int idx, double factor);
	
	double alpha(int idx);
	
	double target(int idx);
	double dot(int idx);
	double snorm(int idx);
}
