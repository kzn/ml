/*
 * Abstract class instead of Interface for efficiency reasons
 */
public abstract class Kernel {
	public abstract double dot(SparseVector x, SparseVector y);
	public abstract double dot(double[] dense, SparseVector x);
}
