package ru.iitp.proling.svm;

import com.spinn3r.log5j.Logger;

import ru.iitp.proling.common.ArrayUtils;

public class CrossValidate {
	int nFold;
	
	public CrossValidate(int nFold){
		this.nFold = nFold;
	}
	
	public double run(WeightVector wv, BinarySolver bs){
		int[] idxs = new int[wv.size()];
		int[] foldStart = new int[nFold + 1];
		double[] scores = new double[wv.size()];
		ArrayUtils.shuffle(idxs, idxs.length);
		for(int i = 0; i != nFold; i++)
			foldStart[i] = i*wv.size()/nFold; // foldEnd[i] = foldStart[i+1]
		
		for(int i = 0; i != nFold; i++){
			int begin = foldStart[i];
			int end = foldStart[i + 1];
			wv.clear();
			WeightVectorSlice wvSlice = new WeightVectorSlice(wv);
			
			for(int j = 0; j < begin; j++)
				wvSlice.add_vec(idxs[j]);
			for(int j = end; j < wv.size(); j++)
				wvSlice.add_vec(idxs[j]);
			
			bs.solve(wvSlice);
			
			for(int j = begin; j != end; j++){
				scores[idxs[j]] = wv.dot(idxs[j]);
				scores[idxs[j]] = scores[idxs[j]] > 0? 1 : -1; 
			}
		}
		
		
		
		
		return 0.0;
	}

}
