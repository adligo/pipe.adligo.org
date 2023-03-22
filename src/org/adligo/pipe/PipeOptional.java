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
  
  /**
   * slightly faster than instance of :)
   * @param b
   * @return
   */
  public static boolean isA(Object b) {
  	if (b == null) {
  		return false;
  	}
  	return PipeOptional.class.equals(b.getClass());
  }
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
    return !delegate.isPresent();
  }

  public boolean isPresent() {
    return delegate.isPresent();
  }
  
  public boolean same(Object ... objects) {
  	for (int i = 0; i < objects.length; i++) {
			if (objects[i] == this) {
				return true;
			}
		}
  	return false;
  }
}
