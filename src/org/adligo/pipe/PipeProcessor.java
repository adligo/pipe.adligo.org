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
  	PipeProcessorCtx ctx = new PipeProcessorCtx();
  	PipeOptional<?> out = PipeOptional.empty();
  	for (I i: collection) {
  		out = process(seg, ctx, i);
  	}
    if (out.isEmpty()) {
      return Optional.empty();
    } else {
      return (Optional<O>) Optional.of(out.get());
    }
  }

  @SuppressWarnings("unchecked")
  public Optional<O> process(I i) {
  	PipeProcessorCtx ctx = new PipeProcessorCtx();
    PipeOptional<?> out = process(seg, ctx,i);
    if (out.isEmpty()) {
      return Optional.empty();
    } else {
      return (Optional<O>) Optional.of(out.get());
    }
  }
  
  @SuppressWarnings("unchecked")
  public Optional<O> process(PipeProcessorCtx ctx, I i) {
    PipeOptional<?> out = process(seg, ctx,i);
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
  		PipeProcessorCtx ctx, Object i) {
    SegmentType t = seg.getType();
    switch (t) {
      case distinct:
        return processDistinct((DistinctSegment) seg, ctx, i);
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
           PipeOptional<Object> ol = processLink((LinkSegment) seg, ctx, i);
           return ol;
      case tail:
          O otf = ((TailSegment<? super Object,O>) seg).apply(i);
          if (otf != null) {
            return PipeOptional.of(otf);
          }
      case tailBi:
      	  PipeOptional<Object> obi = processBiSeg((TailBiSegment) seg, ctx, i);
      	  return obi;
      case tailConsumer:
          ((TailConsumerSegment<? super Object>) seg).accept(i);
        break;
      default:
        throw new IllegalStateException("TODO " + t + " AbstractSegment name is \n\t" 
        		+ seg.getName());
    }
    return PipeOptional.empty();
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <PI,M,PO> PipeOptional<PO> processLink(LinkSegment seg, 
  		PipeProcessorCtx ctx, Object i) {
  	
    AbstractSegment head = seg.getHead();
    PipeOptional po = process(head, ctx, i);
    if (po.isEmpty()) {
      return PipeOptional.empty();
    } else if (seg.isHeadOnly()) {
      return po;
    } else {
      Object m = po.get();
      if (m != null) {
        AbstractSegment tail = seg.getTail();
        return (PipeOptional) process(tail,ctx, m);
      }
    }
    return PipeOptional.empty();
  }
  
  @SuppressWarnings({"unchecked" })
  public <IL, IR, O> PipeOptional<O> processBiSeg(
  		TailBiSegment<IL, IR, O> seg, 
  		PipeProcessorCtx ctx, IR i) {
  	
		O o = null;
		Object accumulator = ctx.getAccumulator(seg);
		if (accumulator == null) {
	  	PipeOptional<IL> po = seg.getIdentityOpt();
	  	if (po.isPresent()) {
			  accumulator = po.get();
	  	}
			o = seg.apply((IL) accumulator, i);
		} else {
			o = seg.apply((IL) accumulator, i);	
		}
		if (o != null) {
  		ctx.setAccumulator(seg, o);
			return PipeOptional.of(o);
		} 
    return PipeOptional.empty();
  }
  

  @SuppressWarnings({"unchecked" })
  public <S, I, O> PipeOptional<O> processDistinct(
  		DistinctSegment<S, I, O> seg, 
  		PipeProcessorCtx ctx, I i) {
  	
		O o = null;
		Set accumulator = ctx.getAccumulator(seg);
		if (accumulator == null) {
			PipeOptional<Set> po = seg.getIdentityOpt();
			accumulator = po.get();
			o = seg.apply((S) accumulator, i);
			ctx.setAccumulator(seg, accumulator);
		} else {
			o = seg.apply((S) accumulator, i);	
		}
		if (o != null) {
			//almost the same as processBi but 
			return PipeOptional.of(o);
		}  
    return PipeOptional.empty();
  }
}
