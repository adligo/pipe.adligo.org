package org.adligo.pipe;

import java.util.function.Function;
import java.util.function.Predicate;

public class FilterHelper {

	public <O> Function<O, PipeOptional<O>> build(Predicate<O> predicate) {
     return (i) -> {
    	if (predicate.test(i)) {
    		return PipeOptional.empty();
    	}
    	return PipeOptional.of(i);
    };
	}
}
