package name.kazennikov.ml.dataset;

import java.io.Reader;

public class SVMLightReaderInteger extends AbstractSVMLightReader {
	
	int label;

	public SVMLightReaderInteger(Reader r) {
		super(r);
	}

	@Override
	protected void parseLabel(String nextToken) {
		label = Integer.parseInt(nextToken);
	}
	
	public int label() {
		return label;
	}
	

}
