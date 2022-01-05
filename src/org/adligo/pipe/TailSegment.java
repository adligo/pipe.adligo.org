package org.adligo.pipe;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class TailSegment<I,O> extends AbstractSegment
implements Function<I, O> {
  private final Function<I, O> tail;
  
  public TailSegment(Function<I,O> tail, Optional<String> nameOpt) {
  	super(nameOpt);
    this.tail = Objects.requireNonNull(tail);
  }

  @SuppressWarnings("unchecked")
	@Override
  public O apply(I i) {
  	if (PipeOptional.isA(i)) {
  		PipeOptional<I> po = (PipeOptional<I>) i;
  		if (po.isPresent()) {
  			I ii = po.get();
  			return tail.apply(ii);
  		}
  	} else {
  	  return tail.apply(i);
  	}
  	return (O) PipeOptional.empty();
  }

  @Override
  public SegmentType getType() {
    return SegmentType.tail;
  }

}
