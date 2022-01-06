package org.adligo.pipe;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public class TailBiSegment<IL,IR,O> extends AbstractSegment
implements BiFunction<IL, IR, O> {
  private final BiFunction<IL, IR, O> tail;
  private final PipeOptional<IL> identity;
  
  public TailBiSegment(BiFunction<IL, IR, O> tail, Optional<String> nameOpt) {
  	this(tail, PipeOptional.empty(), nameOpt);
  }

  public TailBiSegment(BiFunction<IL, IR, O> tail, PipeOptional<IL> identity,
  		Optional<String> nameOpt) {
  	super(nameOpt);
    this.tail = Objects.requireNonNull(tail);
    this.identity = Objects.requireNonNull(identity);
  }
  
  @Override
  public SegmentType getType() {
    return SegmentType.tailBi;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
  public O apply(IL l, IR r) {
  	if (PipeOptional.empty().same(l, r)) {
  		return (O) PipeOptional.empty();
  	}
		if (PipeOptional.isA(l)) {
			return tail.apply((IL) ((PipeOptional) l).get(),r);
		} else {
			return tail.apply(l,r);
		}
  }
  
  public PipeOptional<IL> getIdentityOpt() {
  	return identity;
  }
}
