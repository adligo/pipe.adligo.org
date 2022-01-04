package org.adligo.pipe;

public class PipeFutureExample {

	
  public static void main(String [] args) {
		Pipe<String, Integer> p = Pipe.of(String.class,Integer.class,
				(s) -> Integer.parseInt(s)
		).map((i) -> {
			System.out.println("hey " + i);
			return i;
		}).then((i) -> {
			System.out.println("step 3?");
			return i;
		}).then((i) -> {
			System.out.println("you rock! " + i);
			return i;
		});
		int i = p.apply("123");
		System.out.println("and a PipeFuture can return, ie " + i);
	}
}
