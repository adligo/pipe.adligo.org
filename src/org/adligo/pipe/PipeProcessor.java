package org.adligo.pipe;

import java.util.Objects;
import java.util.Optional;

public class PipeProcessor<I,O> {
  private final AbstractSegment seg;
  
  public PipeProcessor(AbstractSegment seg) {
    this.seg = Objects.requireNonNull(seg);
  }
  
  @SuppressWarnings("unchecked")
  public Optional<O> process(I i) {
    PipeOptional<?> out = process(seg, i);
    if (out.isEmpty()) {
      return Optional.empty();
    } else {
      return (Optional<O>) Optional.of(out.get());
    }
  }
  
  @SuppressWarnings("unchecked")
  public PipeOptional<? super Object> process(AbstractSegment seg,Object i) {
    SegmentType t = seg.getType();
    switch (t) {
      case head:
          O o = ((HeadSegment<? super Object, O>) seg).apply(i);
          if (o != null) {
            return PipeOptional.of(o);
          }
        break;
      case headConsumer:
          ((HeadConsumerSegment<? super Object>) seg).accept(i);
        break;
      case link:
          return processLink((LinkSegment) seg, i);
      case tail:
          O otf = ((TailSegment<? super Object,O>) seg).apply(i);
          if (otf != null) {
            return PipeOptional.of(otf);
          }
      case tailConsumer:
          ((TailConsumerSegment<? super Object>) seg).accept(i);
        break;
      default:
        throw new IllegalStateException("TODO" + t);
    }
    return PipeOptional.empty();
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <PI,M,PO> PipeOptional<PO> processLink(LinkSegment seg, Object i) {
    AbstractSegment head = seg.getHead();
    PipeOptional po = process(head, i);
    if (po.isEmpty()) {
      return PipeOptional.empty();
    } else if (seg.isHeadOnly()) {
      return po;
    } else {
      Object m = po.get();
      if (m != null) {
        AbstractSegment tail = seg.getTail();
        return (PipeOptional) process(tail, m);
      }
    }
    return PipeOptional.empty();
  }
}
