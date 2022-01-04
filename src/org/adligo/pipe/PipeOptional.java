package org.adligo.pipe;

import java.util.Optional;

/**
 * This simply helps distinguish between a users Optional
 * and one that was created in these Pipelines;
 * @author scott
 *
 * @param <T>
 */
public class PipeOptional<T> {
  static PipeOptional<?> EMPTY = new PipeOptional<>();
  
  @SuppressWarnings("unchecked")
  static <T> PipeOptional<T> empty() {
    return (PipeOptional<T>) EMPTY;
  }
    
  @SuppressWarnings("unchecked")
  static <T> PipeOptional<T> of (T i) {
    return new PipeOptional(i);
  }
  private final Optional<T> delegate;

  private PipeOptional() {
    delegate = Optional.empty();
  }

  private PipeOptional(T t) {
    if (t != null) {
      delegate = Optional.of(t);
    } else {
      delegate = Optional.empty();
    }
  }

  public T get() {
    return delegate.get();
  }

  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  public boolean isPresent() {
    return delegate.isPresent();
  }
}
