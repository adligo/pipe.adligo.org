package org.adligo.pipe;

import org.adligo.i.pipe.I_Pipe;

public class PipeReturnExample {

	
  public static void main(String [] args) {
		I_Pipe<String, Integer> p = Pipe.of(String.class,Integer.class,
				(s) -> { return Integer.parseInt(s); }
		).then((i) -> {
			System.out.println("hey " + i);
			return i.doubleValue();
		}).then((i) -> {
			System.out.println("step 3? with a " + i.getClass());
			return i.intValue();
		}).then((i) -> {
			System.out.println("you rock! " + i);
			return i;
		});
		int i = p.apply("123");
		System.out.println("and a PipeFuture can return, ie " + i);
	}
}
