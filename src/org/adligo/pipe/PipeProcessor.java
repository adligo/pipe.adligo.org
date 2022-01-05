package org.adligo.pipe;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class PipeProcessor<I,O> {
  private final AbstractSegment seg;
  
  public PipeProcessor(AbstractSegment seg) {
    this.seg = Objects.requireNonNull(seg);
  }

  @SuppressWarnings("unchecked")
  public Optional<O> process(Collection<I> collection) {
  	Set<AbstractSegment> segStates = new HashSet<AbstractSegment>();
  	PipeOptional<?> out = PipeOptional.empty();
  	for (I i: collection) {
  		out = process(seg, segStates,i);
  	}
    if (out.isEmpty()) {
      return Optional.empty();
    } else {
      return (Optional<O>) Optional.of(out.get());
    }
  }
  
  @SuppressWarnings("unchecked")
  public Optional<O> process(Set<AbstractSegment> segsStarted, I i) {
    PipeOptional<?> out = process(seg, segsStarted,i);
    if (out.isEmpty()) {
      return Optional.empty();
    } else {
      return (Optional<O>) Optional.of(out.get());
    }
  }
  
  /**
   * 
   * @param seg
   * @param segStates This keeps track of the  segements
   *   that have been started as part of this process.
   * @param i
   * @return
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public PipeOptional<? super Object> process(AbstractSegment seg,
  		Set<AbstractSegment> segsStarted, Object i) {
    SegmentType t = seg.getType();
    switch (t) {
      case head:
          O o = ((HeadSegment<? super Object, O>) seg).apply(i);
          segsStarted.add(seg);
          if (o != null) {
            return PipeOptional.of(o);
          }
        break;
      case headConsumer:
          ((HeadConsumerSegment<? super Object>) seg).accept(i);
          segsStarted.add(seg);
        break;
      case link:
           PipeOptional<Object> ol = processLink((LinkSegment) seg, segsStarted, i);
           segsStarted.add(seg);
           return ol;
      case tail:
          O otf = ((TailSegment<? super Object,O>) seg).apply(i);
          segsStarted.add(seg);
          if (otf != null) {
            return PipeOptional.of(otf);
          }
      case tailBi:
      	  PipeOptional<Object> obi = processBiSeg((TailBiSegment) seg, segsStarted, i);
      	  segsStarted.add(seg);
      	  return obi;
      case tailConsumer:
          ((TailConsumerSegment<? super Object>) seg).accept(i);
          segsStarted.add(seg);
        break;
      default:
        throw new IllegalStateException("TODO " + t + " AbstractSegment name is \n\t" 
        		+ seg.getName());
    }
    return PipeOptional.empty();
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <PI,M,PO> PipeOptional<PO> processLink(LinkSegment seg, 
  		Set<AbstractSegment> segsStarted, Object i) {
    AbstractSegment head = seg.getHead();
    PipeOptional po = process(head, segsStarted, i);
    if (po.isEmpty()) {
      return PipeOptional.empty();
    } else if (seg.isHeadOnly()) {
      return po;
    } else {
      Object m = po.get();
      if (m != null) {
        AbstractSegment tail = seg.getTail();
        segsStarted.add(seg);
        return (PipeOptional) process(tail,segsStarted, m);
      }
    }
    return PipeOptional.empty();
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <IL, IR, O> PipeOptional<O> processBiSeg(
  		TailBiSegment<IL, IR, O> seg, 
  		Set<AbstractSegment> segsStarted, IR i) {
    
  	PipeOptional<IL> po = seg.getIdentityOpt();
  	if (po.isPresent()) {
  		O o = null;
  		if (segsStarted.contains(seg)) {
  			o = seg.apply(po.get(), i);
  		} else {
  			o = seg.apply(po.get(), i);	
  		}
  		segsStarted.add(seg);
  		if (o != null) {
  			return PipeOptional.of(o);
  		} 
  	} else {
  		 O o = seg.apply(null, i);
   		if (o != null) {
   			return PipeOptional.of(o);
   		} 
  	}
    return PipeOptional.empty();
  }
}
