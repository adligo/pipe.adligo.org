package org.adligo.pipe;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
  private final Function<B,O> tail;  
  
  public PipeSegment(Function<I,B> head) {
  	this.head = Objects.requireNonNull(head);
  	this.tail = null;
  }

  public PipeSegment(Consumer<I> head) {
  	this.head = new Function<I,B>() {
  		@SuppressWarnings("unchecked")
			public B apply(Object in) { 
  			head.accept((I) in);
  			return null; 
  		} //do nothing
  	};
  	this.tail = null;
  }
  
  public PipeSegment(Function<I,B> head, Function<B,O> tail) {
  	this.head = Objects.requireNonNull(head);
  	this.tail = Objects.requireNonNull(tail);
  }
  
  public PipeSegment(Function<I,B> head, Consumer<B> consumer) {
  	this.head = Objects.requireNonNull(head);
  	Objects.requireNonNull(consumer);
  	this.tail = new Function<B,O>() {
			@Override
			public O apply(B t) {
				if (t != null) {
					consumer.accept(t);
				}
				return null;
			}
  	};  		
  }
  
  public PipeSegment(Function<I,B> head, Function<B, O> tail, Predicate<? super B> predicate) {
  	this.head = Objects.requireNonNull(head);
  	Objects.requireNonNull(tail);
  	Objects.requireNonNull(predicate);
  	this.tail = new Function<B,O>() {
			@Override
			public O apply(B t) {
				if (t != null) {
  				if (predicate.test(t)) {
  					return tail.apply(t);
  				}
				}
				return null;
			}
  	};
  }
  
	public Function<I,B> getHead() {
		return head;
	}

	public Function<B,O> getTail() {
		return tail;
	}

	public boolean hasTail() {
		if (tail == null) {
			return false;
		}
		return true;
	}

	public O apply(I i) {
		B b = head.apply(i);
		if (b == null) {
			return null;
		}
		return tail.apply(b);
	}
}
