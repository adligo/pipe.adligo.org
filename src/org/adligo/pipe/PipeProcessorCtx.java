package org.adligo.pipe;

import java.util.HashMap;

/**
 * This is teh Context for a execution of the PipeProcessor,
 * it is NOT thread safe , as one per call to a pipleline processor's
 * process method should be created.
 *   It holds 
 * @author scott
 *
 */
public class PipeProcessorCtx {
	public static final String NO_ACCUMULATOR = 
			"No Accumulator at this point, and one should be present.";
	/**
	 * This is the map of the tail most part of the segment
	 * to it's accumulator value for particular PipeLine run
	 */
	private HashMap<AbstractSegment, Object> accumulators = new HashMap<>();
	
	/**
	 * A null accumulator means that it's the first time to call
	 * a BiFunction or BiConsumer
	 * @param <T>
	 * @param segment
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAccumulator(AbstractSegment segment) {
		return (T) accumulators.get(segment);
	}
	
	public void setAccumulator(AbstractSegment segment, Object accoumulator) {
		accumulators.put(segment, accoumulator);
	}
}
