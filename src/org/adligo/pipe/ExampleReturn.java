package org.adligo.pipe;

import java.util.Optional;
import java.util.function.Function;

import org.adligo.i_pipe.I_Pipe;

public class ExampleReturn implements Function<String, Integer> {

  public static void main(String[] args) {
    new ExampleReturn();
  }

  public ExampleReturn() {
    I_Pipe<String, Integer> p = new PipeCtx().newPipe(this).then((i) -> {
      System.out.println("hey " + i);
      return i.doubleValue();
    }).then((i) -> {
      System.out.println("step 3? with a " + i.getClass());
      return i.intValue();
    }).then((i) -> {
      System.out.println("yert rock :), ! " + i);
      return i;
    });
    Optional<Integer> i = p.get("123");
    System.out.println("and a PipeFuture can return, ie " + i.get());
  }

  @Override
  public Integer apply(String s) {
    System.out.println("ahmm " + s);
    return Integer.parseInt(s);
  }

}
