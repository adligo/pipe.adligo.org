package org.adligo.pipe;

import java.util.Objects;
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
public class PipeHead<I,O> implements Function<I,O> {
  public static final String UNABLE_TO_PIPE_FROM_A_CONSUMER = "Unable to pipe from a consumer!";
	private final boolean consumer;
	private final Function<I,O> head;
  
  public PipeHead(Function<I,O> head) {
  	this.consumer = false;
  	this.head = Objects.requireNonNull(head);
  }

  public PipeHead(Consumer<I> head) {
  	this.consumer = true;
  	this.head = new Function<I,O>() {
  		@SuppressWarnings("unchecked")
			public O apply(Object in) { 
  			head.accept((I) in);
  			return null; 
  		} 
  	};
  }

	public void allowIfNotConsumer() {
		if (!consumer) {
			return;
		}
		throw new IllegalStateException(UNABLE_TO_PIPE_FROM_A_CONSUMER);
	}
	
	public O apply(I i) {
		return head.apply(i);
	}
	
	public boolean isConsumer() {
		return consumer;
	}
	
	public Function<I,O> toFunction() {
		return head;
	}
}
