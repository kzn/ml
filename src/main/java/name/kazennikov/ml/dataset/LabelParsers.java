package name.kazennikov.ml.dataset;

import java.util.StringTokenizer;

import gnu.trove.list.array.TIntArrayList;

public class LabelParsers {
	public static LabelParser<Integer> intLabelParser() {
		return new LabelParser<Integer>() {
			
			@Override
			public Integer parse(String label) {
				return Integer.parseInt(label);
			}
		};
	}

	
	public static LabelParser<Double> doubleLabelParser() {
		return new LabelParser<Double>() {
			
			@Override
			public Double parse(String label) {
				return Double.parseDouble(label);
			}
		};
	}
	
	public static LabelParser<TIntArrayList> intSetLabelParser() {
		return new LabelParser<TIntArrayList>() {

			@Override
			public TIntArrayList parse(String label) {
				StringTokenizer st = new StringTokenizer(label, ",");
				TIntArrayList labels = new TIntArrayList();
				labels.clear();
				
				while(st.hasMoreTokens()) {
					labels.add(Integer.parseInt(st.nextToken()));
				}
				
				return labels;
			}
		};
	}
}
