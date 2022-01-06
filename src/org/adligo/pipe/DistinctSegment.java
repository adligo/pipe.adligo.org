package org.adligo.pipe;

import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiFunction;

@SuppressWarnings("hiding")
public class DistinctSegment<S,I,O> extends AbstractSegment
implements BiFunction<S, I, O> {
  
  public DistinctSegment(Optional<String> nameOpt) {
  	super(nameOpt);
  }

  @Override
  public SegmentType getType() {
    return SegmentType.distinct;
  }

  @SuppressWarnings("unchecked")
	@Override
  public O apply(S s, I r) {
  	if (((Set<I>) s).add(r)) {
  		return (O) r;
  	}
  	return (O) PipeOptional.empty();
  }
  
  @SuppressWarnings("rawtypes")
	public PipeOptional<Set> getIdentityOpt() {
  	return PipeOptional.of(new HashSet());
  }
}
