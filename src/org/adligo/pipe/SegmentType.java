package org.adligo.pipe;

import java.util.Objects;

public enum SegmentType {
  distinct(DistinctSegment.class),
	head(HeadSegment.class),
  headBi(HeadBiSegment.class),
  headConsumer(HeadConsumerSegment.class),
  link(LinkSegment.class),
  tail(TailSegment.class),
  tailBi(TailBiSegment.class),
  tailConsumer(TailConsumerSegment.class);

  private Class<? super AbstractSegment> clazz;

  @SuppressWarnings("unchecked")
  private SegmentType(Class<? extends AbstractSegment> clazz) {
    this.clazz = (Class<AbstractSegment>) Objects.requireNonNull(clazz);
  }

  public Class<? super AbstractSegment> getClazz() {
    return this.clazz;
  }
}
