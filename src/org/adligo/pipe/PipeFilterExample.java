package org.adligo.pipe;

import java.util.Optional;
import java.util.function.Function;

import org.adligo.i.pipe.I_Pipe;

public class PipeFilterExample implements Function<String, Integer> {

	
  public static void main(String [] args) {
		new PipeFilterExample();
	}
  
  public PipeFilterExample() {
  	I_Pipe<String, Integer> p = Pipe.of(this)
  	.filter((i) -> {
  		 System.out.println("in filter with " + i);
  		 switch (i) {
  		   case 123: return true;
  			 default:
  				 return false;
  		 }
  	}).then((i) -> {
  		System.out.println("hey " + i + " wasn't filtered");
  		return i;
  	});
		Optional<Integer> i = p.get("123");
		System.out.println("and a PipeFuture can return, ie " + i.get() + "\n\n");
		p.supply("456");
  }

	@Override
	public Integer apply(String s) {
		System.out.println("ahmm " + s);
		return Integer.parseInt(s);
	}
  
  
}
