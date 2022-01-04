package org.adligo.pipe;

import java.util.Objects;
import java.util.function.Consumer;

public class HeadConsumerSegment<I> extends AbstractSegment
implements Consumer<I> {
  private final Consumer<I> head;
  
  public HeadConsumerSegment(Consumer<I> head) {
    this.head = Objects.requireNonNull(head);
  }

  @Override
  public void accept(I t) {
    head.accept(t);
  }

  @Override
  public boolean isHeadOnly() {
    return Boolean.TRUE;
  }

  @Override
  public boolean isHeadConsumer() {
    return Boolean.TRUE;
  }

  @Override
  public SegmentType getType() {
    return SegmentType.headConsumer;
  }
}
