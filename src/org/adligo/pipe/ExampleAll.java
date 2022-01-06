package org.adligo.pipe;

public class ExampleAll {

	public static final void println(String s) {
		System.out.println(s);
	}
		
	public static final void main(String [] args) {
		println("Example");
		Example.main(args);
		
		println("\n\nExampleDistinct");
		ExampleDistinct.main(args);
		
		println("\n\nExampleDistinct");
		ExampleDistinct.main(args);
		
		println("\n\nExampleFilter");
		ExampleFilter.main(args);
		
		println("\n\nExampleReduce");
		ExampleReduce.main(args);
		
		println("\n\nExampleReturn");
		ExampleReturn.main(args);
		
		println("\n\nExampleSet");
		ExampleSet.main(args);
	}
}
