package org.adligo.pipe;

import java.util.List;
import java.util.function.Function;

import org.adligo.i.pipe.I_Pipe;

public class ExampleDistinct implements Function<Integer, Integer> {

	
  public static void main(String [] args) {
		new ExampleDistinct();
	}
  
  public ExampleDistinct() {
  	I_Pipe<Integer, Integer> p = Pipe.of(this)
  		.distinct().then((i) -> {
  			System.out.println("hey " + i + " wasn't filtered");
  			return i;
  		});
		p.supply(List.of(1, 1, 2, 3, 4, 5, 55, 6, 7, 8, 99));
  }

	@Override
	public Integer apply(Integer s) {
		return s;
	}
  
  
}
