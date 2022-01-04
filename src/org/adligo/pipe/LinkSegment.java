package org.adligo.pipe;

import java.util.Objects;
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

  public <IL,IR, O> LinkSegment(AbstractSegment head, BiFunction<IL,IR, O> tail) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailBiSegment<>(tail);
  }

  public <I> LinkSegment(AbstractSegment head, Consumer<I> tail) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailConsumerSegment<>(tail);
  }

  public <I,O> LinkSegment(AbstractSegment head, Function<I,O> tail) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailSegment<>(tail);
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
    this.head = new HeadSegment<>(head);
    this.tail = new TailSegment<>(tail);
  }

  public <I,O> LinkSegment(Function<I,O> head, Consumer<O> tail) {
    this.head = new HeadSegment<>(head);
    this.tail = new TailConsumerSegment<>(tail);
  }

  
  public <I,O> LinkSegment(HeadSegment<I,O> head, Consumer<O> tail) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailConsumerSegment<>(tail);
  }

  public <I,O> LinkSegment(HeadSegment<I,O> head, Function<O,O> tail) {
    this.head = Objects.requireNonNull(head);
    this.tail = new TailSegment<>(tail);
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

