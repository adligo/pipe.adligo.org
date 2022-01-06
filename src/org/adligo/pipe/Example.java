package org.adligo.pipe;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.adligo.i.pipe.I_Run;

public class Example {

  public static void main(String[] args) {
    new Example();

//  	ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(50);
//  	Supplier<String> sup = () -> { return queue.poll(); };
//		Stream.generate(sup).map((s) -> {
//			System.out.println("hey at least it's deferred " + s);
//			return Integer.parseInt(s);
//		});
//		
//		queue.add("1");
  }

  public Example() {
    I_Run<String> p = new PipeCtx().newPipe(stepOne(), "one")
//				.filter((i) -> { 
//					System.out.println("in filter " + i);
//					switch(i) {
//					  case 123: return true;
//					  default: return false;
//					}
//				})
        .fork(stepTwo(), "two");
    p.supply(List.of("123", "456", "789"));
  }

  public Function<String, Integer> stepOne() {
    return (s) -> {
      System.out.println("Processing " + s);
      return Integer.parseInt(s);
    };
  }

  public Consumer<Integer> stepTwo() {
    // initialize these first
    I_Run<Integer> p123 = new PipeCtx().newPipe(Integer.class, Integer.class, (i) -> {
      return i++;
    }).then((i) -> {
      System.out.println("\t\tA it's now " + i);
    });

    I_Run<Integer> p456 = new PipeCtx().newPipe(Integer.class, Integer.class, (i) -> {
      return i * i++;
    }).then((i) -> {
      System.out.println("\t\tB it's now " + i);
    });

    // then call at run time
    return (i) -> {
      Integer ii = (Integer) i;
      switch (ii) {
      case 123:
        System.out.println("\tDid A for 123");
        p123.supply(ii);
        break;
      case 456:
        System.out.println("\tDid B for 456");
        p456.supply(ii);
        break;
      default:
        System.out.println("\tDid default");
      }
    };
  }
}
