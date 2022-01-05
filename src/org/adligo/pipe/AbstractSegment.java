package org.adligo.pipe;

import java.util.Objects;
import java.util.Optional;

import org.adligo.i.pipe.I_Named;

public abstract class AbstractSegment implements I_Named {

  public static final String IS_HEAD_ONLY_IS_FALSE_AND_HAS_TAIL_IS_FALSE_PROGRAMMING_ISSUE = 
      "isHeadOnly is false and hasTail is false, programming issue?\n\t%s";
  public static final String CONSUMER_NOT_MAPPABLE = 
      "The head of the pipe segment is a Consumer, not able to Mao!";
  public static final String TAIL_CONSUMER_NOT_MAPPABLE = 
      "The tail of the pipe segment is a Consumer, not able to Mao!";
  
  private final String name;

  public AbstractSegment() {
  	this.name = this.toString();
  }
  
  public AbstractSegment(String name) {
  	this.name = Objects.requireNonNull(name);
  }
 
  public AbstractSegment(Optional<String> nameOpt) {
  	Objects.requireNonNull(nameOpt);
  	if (nameOpt.isPresent()) {
  	  this.name = Objects.requireNonNull(nameOpt.get());
  	} else {
  		this.name = toString();
  	}
  }
  
  /**
   * Throw an illegal state Exception with a message
   */
  public void checkMappable() {
    if (isHeadOnly()) {
      if (isHeadConsumer()) {
        throw new IllegalStateException(CONSUMER_NOT_MAPPABLE);
      } 
    } else if (hasTail()) {
      if (isTailConsumer()) {
        throw new IllegalStateException(TAIL_CONSUMER_NOT_MAPPABLE);
      }
    } else {
      throw new IllegalStateException(
          String.format(IS_HEAD_ONLY_IS_FALSE_AND_HAS_TAIL_IS_FALSE_PROGRAMMING_ISSUE, 
              this.getClass().toString()));
    }
  }
  /**
   * Override if your tail is a consumer
   * @return
   */
  public boolean isHeadOnly() {
    return false;
  }


  public boolean isHeadBiFunction() {
    return false;
  }
  
  /**
   * Although the top of the head (faka the very first function
   * in a Pipe) will never be a BiFunction,
   * the head of a subsequent Segement can be.
   * @return
   */
  public boolean isHeadConsumer() {
    return false;
  }

  public boolean isHeadFunction() {
    return false;
  }

  /**
   * Override if your tail is a biFunction
   * @return
   */
  public boolean isTailBiFunction() {
    return false;
  }

  /**
   * Override if your tail is a consumer
   * @return
   */
  public boolean isTailConsumer() {
    return false;
  }

  /**
   * Override if your tail is a function
   * @return
   */
  public boolean isTailFunction() {
    return false;
  }

  /**
   * Override if your tail is a function
   * @return
   */
  public boolean isTailSegment() {
    return false;
  }
  
  public abstract SegmentType getType();
  
  public String getName() {
  	return name;
  }
  
  public boolean hasTail() {
    return false;
  }
}
