package org.adligo.pipe;

import java.util.List;
import java.util.function.Function;

import org.adligo.i.pipe.I_Pipe;

public class PipeListExample implements Function<Integer, String> {

	
  public static void main(String [] args) {
		new PipeListExample();
	}
  
  public PipeListExample() {
  	/*
  	I_Pipe<Integer, List<String>> p = Pipe.of(this)
  		.distinct().map((i) -> {
  			System.out.println("hey " + i + " wasn't filtered");
  			return i;
  		}).toList();
		List<String> slist = p.get(List.of(1, 1, 2, 3, 4, 5, 55,  55, 55, 6, 7, 8, 99));
		for (String s: slist) {
			System.out.println("hey you guys " + s);
		}
		System.out.println("hey you guys \n\n");
		slist = p.get(List.of(1, 1, 5,  55, 55));
		for (String s: slist) {
			System.out.println("hey you guys " + s);
		}
		*/
  }

	@Override
	public String apply(Integer s) {
		return "" + s;
	}
  
  
}
