package org.adligo.pipe;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.adligo.i.pipe.I_Pipe;

public class ExampleReduce implements Function<Integer, String> {

	
  public static void main(String [] args) {
		new ExampleReduce();
	}
  
  public ExampleReduce() {
  	I_Pipe<Integer, Double> p = Pipe.of(this)
  		.map((s) -> {
  			System.out.println("hey " + s + " wasn't filtered");
  			return Double.parseDouble(s);
  		}).reduce((Double) 0.0, (i,a) -> { return i + a; });
  		
//  		.map(new Function<Double,Double>() {
//  			double sum = 0.0;
//  			
//				@Override
//				public Double apply(Double t) {
//					sum = sum + t;
//					return sum;
//				}
//  			
//  		});
  	
  	List<Integer> list = List.of(1, 1, 2, 3, 4, 5, 55,  55, 55, 6, 7, 8, 99);
		Optional<Double> d = p.get(list);
		System.out.println("got result " + d.get());
		
		d = p.get(List.of(1, 1, 2));
		System.out.println("got result 2 " + d.get());
  }

	@Override
	public String apply(Integer s) {
		return "" + s;
	}
  
  
}
