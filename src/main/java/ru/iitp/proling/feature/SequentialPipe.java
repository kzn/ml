package ru.iitp.proling.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;

public class SequentialPipe extends Pipe{
	
	List<Pipe> pipes = new ArrayList<Pipe>();
	
	public SequentialPipe(List<Pipe> pipes){
		this.pipes.addAll(pipes);
	}
	
	public SequentialPipe(Pipe... pipes){
		this.pipes.addAll(Arrays.asList(pipes));
	}
	
	public void add(Pipe pipe){
		pipes.add(pipe);
	}
	
	public Instance pipe(Instance carrier){
		
		for(Pipe p : pipes)
			carrier = p.pipe(carrier);
		
		return carrier;
		
	}

}
