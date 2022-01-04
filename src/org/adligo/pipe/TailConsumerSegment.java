package org.adligo.pipe;

import java.util.Objects;
import java.util.function.Consumer;

public class TailConsumerSegment<I> extends AbstractSegment
implements Consumer<I> {
  private final Consumer<I> tail;
  
  public TailConsumerSegment(Consumer<I> tail) {
    this.tail = Objects.requireNonNull(tail);
  }

  @Override
  public SegmentType getType() {
    return SegmentType.tailBi;
  }

  @Override
  public void accept(I i) {
    if (i != null) {
      tail.accept(i);
    }
  }

}
