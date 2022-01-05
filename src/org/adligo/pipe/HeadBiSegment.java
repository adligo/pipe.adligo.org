package org.adligo.pipe;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public class HeadBiSegment<IL,IR,O> extends AbstractSegment
implements BiFunction<IL, IR,O> {
  private final BiFunction<IL, IR,O> head;
  
  public HeadBiSegment(BiFunction<IL, IR,O> head) {
    this.head = Objects.requireNonNull(head);
  }

  public HeadBiSegment(BiFunction<IL, IR,O> head, Optional<String> nameOpt) {
  	super(nameOpt);
    this.head = Objects.requireNonNull(head);
  }
  
  @Override
  public boolean isHeadOnly() {
    return Boolean.TRUE;
  }

  @Override
  public boolean isHeadBiFunction() {
    return Boolean.TRUE;
  }

  @Override
  public O apply(IL t, IR u) {
    return head.apply(t, u);
  }

  @Override
  public SegmentType getType() {
    return SegmentType.headBi;
  }
}
