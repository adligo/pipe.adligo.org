package org.adligo.pipe;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PipeSink<T> implements Consumer<T>, Supplier<T> {
  private final AtomicReference<T> ar = new AtomicReference<T>();
	private final Predicate<T> predicate;
	
	public PipeSink(Predicate<T> predicate) {
    this.predicate = Objects.requireNonNull(predicate);
	}

	@Override
	public T get() {
		return ar.get();
	}

	@Override
	public void accept(T t) {
		if (predicate.test(t)) {
			ar.set(t);
		}
	}
	
	
}
