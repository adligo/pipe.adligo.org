package org.adligo.pipe;

import java.util.Objects;
import java.util.function.BiFunction;

public class TailBiSegment<IL,IR,O> extends AbstractSegment
implements BiFunction<IL, IR, O> {
  private final BiFunction<IL, IR, O> tail;
  
  public TailBiSegment(BiFunction<IL, IR, O> tail) {
    this.tail = Objects.requireNonNull(tail);
  }

  @Override
  public SegmentType getType() {
    return SegmentType.tailBi;
  }

  @Override
  public O apply(IL l, IR r) {
    return tail.apply(l,r);
  }
}
