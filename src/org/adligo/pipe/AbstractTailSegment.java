package org.adligo.pipe;

import java.util.Objects;

/**
 * 
 * @author scott
 *
 * @param <I> input to the head function
 * @param <M> output of the head function (M for Map)
 */
public abstract class AbstractTailSegment<I, M> extends AbstractSegment {
  protected final HeadSegment<I, M> head;

  public AbstractTailSegment(HeadSegment<I, M> head) {
    this.head = Objects.requireNonNull(head);
  }

  public HeadSegment<I, M> getHead() {
    return head;
  }

  @Override
  public boolean isHeadOnly() {
    return Boolean.FALSE;
  }

  @Override
  public boolean isHeadFunction() {
    return Boolean.TRUE;
  }

  @Override
  public boolean hasTail() {
    return Boolean.TRUE;
  }

}
