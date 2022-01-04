package org.adligo.pipe;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class Pipe<I> //implements I_Pipe<T> 
{
  @SuppressWarnings("unchecked")
	public static final <PI,PO> Pipe<PI> of(Consumer<PI> consumer) {
  	return new Pipe(consumer);
  }
  @SuppressWarnings("unchecked")
	public static final <PI,PO> Pipe<PI> of(Function<PI,PO> fun) {
  	return new Pipe(fun);
  }
  
  public static void main(String [] args) {
		Pipe<String> p = Pipe.of(
		  new Function<String,Integer>() {

				@Override
				public Integer apply(String t) {
					return Integer.parseInt(t);
				}
		  	
		  }
		).map((i) -> {
			System.out.println("hey " + i);
			return i;
		}).then((i) -> {
			System.out.println("step 3?");
		})
		.then((i) -> {
			System.out.println("you rock! " + i);
		});
		p.add("123");
	}
	
	private AtomicReference<PipeSegment<I,?,?>> segment = new AtomicReference<>();
	
	public Pipe(Consumer<I> consumer) {
		segment.set(new PipeSegment(consumer));
	}

	public <B> Pipe(Function<I,B> head) {
		segment.set(new PipeSegment(head));
	}
	
	public void add(I in) {
		segment.get().apply(in);
	}

	public <B,R> Pipe<I> then(Function<? super B, ? extends R> mapper) {
		return map(mapper);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <B> Pipe<I>  then(Consumer<? super B> consumer) {
		PipeSegment seg = segment.get();
		Function<I,B> head = (Function<I,B>) seg.getHead();
		if (seg.hasTail()) {
			PipeSegment newTail = new PipeSegment(seg.getTail(),consumer);
			segment.set(new PipeSegment<>(head, newTail));
		} else {
			segment.set(new PipeSegment(head, consumer));			
		}
		return this;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <B,R> Pipe<I> map(Function<? super B, ? extends R> mapper) {
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
}
