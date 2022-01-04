package org.adligo.pipe;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class Pipe<I,O> 
implements Function<I,O>
//implements I_Pipe<T> 
{
	
	public static final <PI,PO> Pipe<PI,PO> of(Consumer<PI> consumer) {
  	return new Pipe<PI,PO>(consumer);
  }

	public static final <PI> Pipe<PI,Void> of(Class<PI> inClazz, Consumer<PI> consumer) {
  	return new Pipe<PI,Void>(consumer);
  }
	
	public static final <PI,PO> Pipe<PI,PO> of(Function<PI,PO> fun) {
  	return new Pipe<PI,PO>(fun);
  }
	
	public static final <PI,PO, FR> Pipe<PI,PO> of(Class<PI> inClazz, Class<PO> outClazz, 
			Function<PI,PO> fun) {
  	return new Pipe<PI,PO>(fun);
  }

	
	private AtomicReference<PipeSegment<I,?,?>> segment = new AtomicReference<>();
	
	@SuppressWarnings("unchecked")
	public Pipe(Consumer<I> consumer) {
		segment.set(new PipeSegment(consumer));
	}

	@SuppressWarnings("unchecked")
	public <B> Pipe(Function<I,B> head) {
		segment.set(new PipeSegment(head));
	}
	

	public <B,R> Pipe<I,R> decision(Consumer<? super B> consumer) {
		return then(consumer);
	}
	
	public <B,R> Pipe<I,O> then(Function<? super B, ? extends R> mapper) {
		return map(mapper);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <B, R> Pipe<I,R>  then(Consumer<? super B> consumer) {
		PipeSegment seg = segment.get();
		Function<I,B> head = (Function<I,B>) seg.getHead();
		if (seg.hasTail()) {
			PipeSegment newTail = new PipeSegment(seg.getTail(),consumer);
			segment.set(new PipeSegment(head, newTail));
		} else {
			segment.set(new PipeSegment(head, consumer));			
		}
		return (Pipe) this;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <B,R> Pipe<I,O> map(Function<? super B, ? extends R> mapper) {
		PipeSegment seg = segment.get();
		Function<I,B> head = (Function<I,B>) seg.getHead();
		if (seg.hasTail()) {
			PipeSegment newTail = new PipeSegment(seg.getTail(),mapper);
			segment.set(new PipeSegment<I, B,R>(head, newTail));
		} else {
			segment.set(new PipeSegment(head, mapper));			
		}
		return (Pipe) this;
	}
	
	@SuppressWarnings("unchecked")
	public O apply(I in) {
		return (O) segment.get().apply(in);
	}
	
	public void supply(I in) {
		segment.get().apply(in);
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })	
	public void supply(Collection<I> in) {
		PipeSegment seg = segment.get();
		for (I i: in) {
			seg.apply(i);
		}
	}	
}
