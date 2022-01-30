package org.adligo.pipe;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.adligo.i_pipe.I_Pipe;

public class ExampleList implements Function<Integer, String> {

	
  public static void main(String [] args) {
		new ExampleList();
	}
  
  public ExampleList() {
  	I_Pipe<Integer, List<String>> p = new PipeCtx().newPipe(this, "1")
  		.distinct("2").map((i) -> {
  			System.out.println("hey " + i + " wasn't filtered");
  			return i;
  		}, "3").toList("4");
		Optional<List<String>> slistOpt = p.get(List.of(1, 1, 2, 3, 4, 5, 55,  55, 55, 6, 7, 8, 99));
		
		List<String> slist = slistOpt.get();
		
		for (String s: slist) {
			System.out.println("hey you guys " + s);
		}
		System.out.println("hey you guys \n\n");
		slistOpt = p.get(List.of(1, 1, 5,  55, 55));
		
		slist = slistOpt.get();
		for (String s: slist) {
			System.out.println("hey you guys " + s);
		}
  }

	@Override
	public String apply(Integer s) {
		return "" + s;
	}
  
  
}
