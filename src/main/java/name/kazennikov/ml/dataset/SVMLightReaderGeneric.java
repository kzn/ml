package name.kazennikov.ml.dataset;

import java.io.Reader;

public abstract class SVMLightReaderGeneric<E> extends AbstractSVMLightReader {

	public SVMLightReaderGeneric(Reader r) {
		super(r);
	}
	
	public abstract E label();


}
