package org.adligo.pipe;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 
 * @author scott
 *
 * @param <I> input
 * @param <B> output of head, intput to tail
 * @param <O> output
 */
public class PipeSegment<I,B,O> implements Function<I,O> {
	
  private final Function<I,B> head;
  private final Optional<Function<B,O>> tail;  
  
  public PipeSegment(Function<I,B> head) {
  	this.head = Objects.requireNonNull(head);
  	this.tail = Optional.empty();
  }

  public PipeSegment(Consumer<I> head) {
  	this.head = new Function<I,B>() {
  		@SuppressWarnings("unchecked")
			public B apply(Object in) { 
  			head.accept((I) in);
  			return null; 
  		} //do nothing
  	};
  	this.tail = Optional.empty();
  }
  
  public PipeSegment(Function<I,B> head, Function<B,O> tail) {
  	this.head = Objects.requireNonNull(head);
  	this.tail = Optional.of(Objects.requireNonNull(tail));
  }

  public PipeSegment(Function<I,B> head, Consumer<B> consumer) {
  	this.head = Objects.requireNonNull(head);
  	Objects.requireNonNull(consumer);
  	Function<B,O> tailConsumer = new Function<B,O>() {
			@Override
			public O apply(B t) {
				consumer.accept(t);
				return null;
			}
  	};
  	this.tail = Optional.of(tailConsumer);
  }
  
	public Function<I,B> getHead() {
		return head;
	}

	public Function<B,O> getTail() {
		return tail.get();
	}
  
	public boolean hasTail() {
		if (tail.isPresent()) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public O apply(I i) {
		B b = head.apply(i);
		if (tail.isPresent()) {
			return tail.get().apply(b);
		} else {
			return (O) b;
		}
	}
}
