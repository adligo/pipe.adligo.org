package org.adligo.pipe;

import java.nio.channels.Pipe.SinkChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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


	private PipeHead<I,O> head;
	private Optional<PipeSegment<I,?,O>> segment;
	
	@SuppressWarnings("unchecked")
	public Pipe(Consumer<I> consumer) {
		this.head = new PipeHead(consumer);
		segment = Optional.empty();
	}

	@SuppressWarnings("unchecked")
	public <B> Pipe(Function<I,B> head) {
		this.head = new PipeHead(head);
		segment = Optional.empty();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <B> Pipe(Function<I,B> head, Function<B,O> tail) {
		this.head = new PipeHead(head);
		PipeSegment ps = new PipeSegment<>(head, tail);
		segment = Optional.of(ps);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <B> Pipe(Function<I,B> head, Consumer<B> tail) {
		this.head = new PipeHead(head);
		PipeSegment ps = new PipeSegment<>(head, tail);
		segment = Optional.of(ps);
	}
	
	public O apply(I in) {
		if (segment.isPresent()) {
			PipeSegment ps = segment.get();
			return (O) ps.apply(in);
		}
		return head.apply(in);
	}

	@Override
	public I_Run<I> decision(Consumer<? super O> consumer) {
		return then(consumer);
	}
	
  public I_Pipe<I,O> distinct() {
  	return this;
  }
  
	@Override
	public <B,R> I_Pipe<I,R> decision(Function<? super O, ? extends R> mapper) {
		return map(mapper);
	}
	
	public O get(I in) {
		return apply(in);
	}	
	
	@Override
	public I_Pipe<I,O> filter(Predicate<? super O> predicate) {
		
		head.allowIfNotConsumer();
		Function<O,O> d = (i) -> { return i; };
		if (segment.isEmpty()) {
			segment = Optional.of(new PipeSegment(head, d, predicate));
			return (I_Pipe) this;
		} else {
			PipeSegment seg = segment.get();
			PipeSegment newTail = new PipeSegment(seg.getTail(),d, predicate);
			segment = Optional.of(new PipeSegment(head, newTail));
		}
		return (I_Pipe) this;
	}
	
	public <R> I_Pipe<I,R> then(Function<? super O, ? extends R> mapper) {
		return map(mapper);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public I_Run<I> then(Consumer<? super O> consumer) {
		head.allowIfNotConsumer();
		if (segment.isEmpty()) {
			segment = Optional.of(new PipeSegment(head, consumer));
			return (I_Pipe) this;
		} else {
			PipeSegment seg = segment.get();
			PipeSegment newTail = new PipeSegment(seg.getTail(), consumer);
			segment = Optional.of(new PipeSegment(head, newTail));
		}
		return (I_Run) this;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	//<R> I_Pipe<R> map(Function<? super T, ? extends R> mapper);
	public <R> I_Pipe<I,R> map(Function<? super O, ? extends R> mapper) {
		head.allowIfNotConsumer();
		if (segment.isEmpty()) {
			segment = Optional.of(new PipeSegment(head, mapper));
			return (I_Pipe) this;
		} else {
			PipeSegment seg = segment.get();
			PipeSegment newTail = new PipeSegment(seg.getTail(), mapper);
			segment = Optional.of(new PipeSegment(head, newTail));
		}
		return (I_Pipe) this;
	}
	
	
	public void supply(I in) {
		supply(List.of(in));
	}	

	public void supply(I ... in) {
		supply(List.of(in));
	}	
	
	public void supply(Collection<I> in) {
		for(I i: in) {
			apply(i);
		}
	}	
	
}
