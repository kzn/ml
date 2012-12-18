package name.kazennikov.ml.dataset;

public interface LabelParser<E> {
	public E parse(String label);
}
