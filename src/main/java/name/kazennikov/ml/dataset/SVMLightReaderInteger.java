package name.kazennikov.ml.dataset;

import java.io.Reader;

public class SVMLightReaderInteger extends AbstractSVMLightReader {
	
	int label;

	public SVMLightReaderInteger(Reader r) {
		super(r);
	}

	@Override
	protected void parseLabel(String nextToken) {
		if(nextToken.charAt(0) == '+') {
			nextToken = nextToken.substring(1);
		}
		label = Integer.parseInt(nextToken);
	}
	
	public int label() {
		return label;
	}
	

}
