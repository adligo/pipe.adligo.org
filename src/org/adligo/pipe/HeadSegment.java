package org.adligo.pipe;

import java.util.Objects;
import java.util.function.Function;

public class HeadSegment<I,O> extends AbstractSegment
implements Function<I, O> {
  private final Function<I, O> head;
  
  public HeadSegment(Function<I,O> head) {
    this.head = Objects.requireNonNull(head);
  }

  @Override
  public O apply(I t) {
    return head.apply(t);
  }

  @Override
  public boolean isHeadOnly() {
    return Boolean.TRUE;
  }

  @Override
  public boolean isHeadFunction() {
    return Boolean.TRUE;
  }

  @Override
  public SegmentType getType() {
    return SegmentType.head;
  }
}
