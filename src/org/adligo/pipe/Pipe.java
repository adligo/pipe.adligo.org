package org.adligo.pipe;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import org.adligo.i.pipe.I_Pipe;
import org.adligo.i.pipe.I_Run;

public class Pipe<I,O> 
implements I_Pipe<I,O>
//implements I_Pipe<T> 
{
	
	public static final <PI,PO> I_Pipe<PI,PO> of(Consumer<PI> consumer) {
  	return new Pipe<PI,PO>(consumer);
  }

	public static final <PI> I_Run<PI> of(Class<PI> inClazz, Consumer<PI> consumer) {
  	return new Pipe<PI,Void>(consumer);
  }
	
	public static final <PI,PO> I_Pipe<PI,PO> of(Function<PI,PO> fun) {
  	return new Pipe<PI,PO>(fun);
  }
	
	public static final <PI,PO> I_Pipe<PI,PO> of(Class<PI> inClazz, Class<PO> outClazz, 
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
	
	@Override
	public <B,R> I_Run<I> decision(Consumer<? super B> consumer) {
		return then(consumer);
	}
	
	@Override
	public <B,R> I_Pipe<I,R> decision(Function<? super O, ? extends R> mapper) {
		return map(mapper);
	}
	
	public <B,R> I_Pipe<I,R> then(Function<? super O, ? extends R> mapper) {
		return map(mapper);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <B> I_Run<I> then(Consumer<? super B> consumer) {
		PipeSegment seg = segment.get();
		Function<I,B> head = (Function<I,B>) seg.getHead();
		if (seg.hasTail()) {
			PipeSegment newTail = new PipeSegment(seg.getTail(),consumer);
			segment.set(new PipeSegment(head, newTail));
		} else {
			segment.set(new PipeSegment(head, consumer));			
		}
		return (I_Run<I>) this;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	//<R> I_Pipe<R> map(Function<? super T, ? extends R> mapper);
	public <B,R> I_Pipe<I,R> map(Function<? super O, ? extends R> mapper) {
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
