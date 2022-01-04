package org.adligo.pipe;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PipeExample {

  public static void main(String [] args) {
  	new PipeExample();
	}
  
  public PipeExample() {
		Pipe<String, Void> p = Pipe.of(String.class, Integer.class, stepOne())
				.decision(stepTwo());
		p.supply(List.of("123","456","789"));
  }
  
  public Function<String,Integer> stepOne() {
  	return (s) -> { 
  		System.out.println("Processing " + s);
			return Integer.parseInt(s); 
		};
  }
  
  public Consumer<Integer> stepTwo() {
  	//initialize these first
  	Pipe<Integer, Void> p123 = Pipe.of(Integer.class,Integer.class, 
  			  (i) -> { return i++; })
  			.then((i) -> { 
  				System.out.println("\t\tA it's now " + i);
  			});

  	Pipe<Integer, Void> p456 = Pipe.of(Integer.class,Integer.class,  
  			  (i) -> { return i * i++; })
  			.then((i) -> { 
  				System.out.println("\t\tB it's now " + i);
  			});
  	
  	//then call at run time
  	return (i) -> {
			Integer ii = (Integer) i;
			switch (ii) {
			  case 123: System.out.println("\tDid A for 123"); 
			    p123.supply(i);
			    break;
			  case 456: System.out.println("\tDid B for 456"); 
			    p456.supply(i);
			    break;
			  default:
			  	System.out.println("\tDid default");
			}
		};
  }
}
