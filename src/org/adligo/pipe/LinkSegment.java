package org.adligo.pipe;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 
 * @author scott
 *
 * @param <I> input to the head functino
 * @param <M> outpu tfrom the head function, input to the tail function
 *   M for map :)
 * @param <O> output from the tail function
 */
public class LinkSegment extends AbstractSegment {
  private final AbstractSegment head;
  private final AbstractSegment tail;
  

  
  public LinkSegment(AbstractSegment head, AbstractSegment tail) {
    this.head = Objects.requireNonNull(head);
    this.tail = Objects.requireNonNull(tail);
  }

  public <IL,IR, O> LinkSegment(AbstractSegment head, PipeOptional<O> identityOpt, BiFunction<IL,IR, O> tail) {
    this(head, identityOpt, tail, Optional.empty());
  }
  
  @SuppressWarnings("unchecked")
	public <IL,IR, O> LinkSegment(AbstractSegment head, PipeOptional<O> identityOpt, BiFunction<IL,IR, O> tail, 
  		Optional<String> nameOpt) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailBiSegment(tail, identityOpt, nameOpt);
  }

  public <I> LinkSegment(AbstractSegment head, Consumer<I> tail) {
    this(head,tail, Optional.empty());
  }
  
  public <I> LinkSegment(AbstractSegment head, Consumer<I> tail, Optional<String> nameOpt) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailConsumerSegment<>(tail, nameOpt);
  }

  public <I,O> LinkSegment(AbstractSegment head, Function<I,O> tail) {
    this(head, tail, Optional.empty());
  }
  
  public <I,O> LinkSegment(AbstractSegment head, Function<I,O> tail, Optional<String> nameOpt) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailSegment<>(tail, nameOpt);
  }

  public <IL,IR, O> LinkSegment(BiFunction<IL,IR, O> head, AbstractSegment tail) {
    this.head = new HeadBiSegment<>(head);
    this.tail = Objects.requireNonNull(tail);
  }

  public <I> LinkSegment(Consumer<I> head,AbstractSegment tail) {
    this.head = new HeadConsumerSegment<>(head);
    this.tail = Objects.requireNonNull(tail);
  }

  public <I,O> LinkSegment(Function<I,O> head, AbstractSegment tail) {
    this.head = new HeadSegment<>(head);
    this.tail = Objects.requireNonNull(tail);
  }

  public <I,M,O> LinkSegment(Function<I,M> head, Function<M,O> tail) {
    this(head, Optional.empty(), tail, Optional.empty());
  }
  
  public <I,M,O> LinkSegment(Function<I,M> head, 
  		Optional<String> headNameOpt,
  		Function<M,O> tail, Optional<String> tailNameOpt) {
    this.head = new HeadSegment<>(head, headNameOpt);
    this.tail = new TailSegment<>(tail, tailNameOpt);
  }

  public <I,O> LinkSegment(Function<I,O> head, Consumer<O> tail) {
    this(head, Optional.empty(), tail, Optional.empty());
  }
  
  public <I,O> LinkSegment(Function<I,O> head, Optional<String> headNameOpt, 
  		Consumer<O> tail, Optional<String> tailNameOpt) {
    this.head = new HeadSegment<>(head, headNameOpt);
    this.tail = new TailConsumerSegment<>(tail, tailNameOpt);
  }

  public <I,O> LinkSegment(HeadSegment<I,O> head, Consumer<O> tail) {
    this(head, tail, Optional.empty());
  }
  
  public <I,O> LinkSegment(HeadSegment<I,O> head, Consumer<O> tail, Optional<String> nameOpt) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailConsumerSegment<>(tail, nameOpt);
  }

  public <I,O> LinkSegment(HeadSegment<I,O> head, Function<O,O> tail) {
    this(head, tail, Optional.empty());
  }
  
  public <I,O> LinkSegment(HeadSegment<I,O> head, Function<O,O> tail, Optional<String> nameOpt) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailSegment<>(tail, nameOpt);
  }

  @Override
  public boolean isTailSegment() {
    return Boolean.TRUE;
  }

  public AbstractSegment getHead() {
    return head;
  }
  
  public AbstractSegment getTail() {
    return tail;
  }

  @Override
  public SegmentType getType() {
    return SegmentType.link;
  }

  @Override
  public boolean hasTail() {
    return Boolean.TRUE;
  }
  
  
}

