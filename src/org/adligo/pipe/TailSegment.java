package org.adligo.pipe;

import java.util.Objects;
import java.util.function.Function;

public class TailSegment<I,O> extends AbstractSegment
implements Function<I, O> {
  private final Function<I, O> tail;
  
  public TailSegment(Function<I,O> tail) {
    this.tail = Objects.requireNonNull(tail);
  }

  @Override
  public O apply(I t) {
    return tail.apply(t);
  }


  @Override
  public SegmentType getType() {
    return SegmentType.tail;
  }

}
