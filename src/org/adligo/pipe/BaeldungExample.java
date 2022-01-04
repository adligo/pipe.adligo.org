package org.adligo.pipe;

import java.util.Arrays;
import java.util.List;

public class BaeldungExample {

  public static void main(String [] args) {
    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
    int result = numbers
      .stream()
      .reduce(0, (subtotal, element) -> subtotal + element);
    
    
    List<String> ss = Arrays.asList("1","2","3");
    int result2 = ss
      .stream()
      .map(s -> { return Integer.parseInt(s); })
      .reduce(0, (subtotal, element) -> subtotal + element);
    
  }
}
