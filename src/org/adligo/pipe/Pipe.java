package org.adligo.pipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.adligo.i.pipe.I_Pipe;
import org.adligo.i.pipe.I_Run;

public class Pipe<I, O> implements I_Pipe<I, O>
//implements I_Pipe<T> 
{  
  private final AtomicReferenceExt<AbstractSegment> ar = new AtomicReferenceExt<>();

  public Pipe(Consumer<I> consumer) {
    this(consumer, Optional.empty());
  }

  public Pipe(Consumer<I> consumer, Optional<String> nameOpt) {
    ar.set(new HeadConsumerSegment<>(consumer, nameOpt));
  }
  
  public <B> Pipe(Function<I, B> head) {
    this(head, Optional.empty());
  }

  public <B> Pipe(Function<I, B> head, Optional<String> name) {
    ar.set(new HeadSegment<>(head, name));
  }
  
  public <B> Pipe(Function<I, B> head, Function<B, O> tail) {
    this(head, Optional.empty(), tail, Optional.empty());
  }

  public <B> Pipe(Function<I, B> head, Optional<String> headNameOpt, 
  		Function<B, O> tail, Optional<String> tailNameOpt) {
    ar.set(new LinkSegment(head, headNameOpt, tail, tailNameOpt));
  }

  public <B> Pipe(Function<I, B> head, Consumer<B> tail) {
    ar.set(new LinkSegment(head, tail));
  }

  public <B> Pipe(Function<I, B> head, Optional<String> headNameOpt, 
  		Consumer<B> tail, Optional<String> tailNameOpt) {
    ar.set(new LinkSegment(head, headNameOpt, tail, tailNameOpt));
  }
	
  public I_Pipe<I, O> distinct() {
    return distinct(Optional.empty());
  }

  public I_Pipe<I, O> distinct(String name) {
  	return distinct(Optional.of(Objects.requireNonNull(name)));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
	public I_Pipe<I, O> distinct(Optional<String> nameOpt) {
    AbstractSegment seg = ar.get();
    seg.checkMappable();
    SegmentType t = seg.getType();
    switch (t) {
      case head:
          ar.set(new LinkSegment(seg, new DistinctSegment(nameOpt)));
        break;
      case tail:
      case link:
          LinkSegment tfSeg = (LinkSegment) seg;
          AbstractSegment oldHead = tfSeg.getHead();
          AbstractSegment oldTail = tfSeg.getTail();
          ar.set(new LinkSegment(oldHead, 
          		new LinkSegment(oldTail, new DistinctSegment(nameOpt))));
        break;
      default:
        throw new IllegalStateException("TODO " + t);
    }
    return (I_Pipe) this;
  }
  
	@Override
	public I_Pipe<I, O> filter(Predicate<? super O> predicate) {
		return filter(predicate, Optional.empty());
	}
	
	@Override
	public I_Pipe<I, O> filter(Predicate<? super O> predicate, String name) {
		return filter(predicate, Optional.of(Objects.requireNonNull(name)));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public I_Pipe<I, O> filter(Predicate<? super O> predicate, Optional<String> nameOpt) {
    AbstractSegment seg = ar.get();
		seg.checkMappable();
		FilterHelper fh = new FilterHelper();
    SegmentType t = seg.getType();
    switch (seg.getType()) {
       case head:
      	   ar.set(new LinkSegment(seg, fh.build(predicate), nameOpt));
      	 break;
       case link:
      	   LinkSegment ls = (LinkSegment) seg;
    	     ar.set(new LinkSegment(ls.getHead(), 
    	    		 new LinkSegment(ls.getTail(), fh.build(predicate), nameOpt)));
    	   break;
      	 //intentional fails
       case headBi:
       case headConsumer:
      	 throw new IllegalStateException(String.format("Invalid type %s " +  
      			 " on fuction named %s at this locaton.", "" + t, seg.getName()));
       default:
      	 throw new IllegalStateException("TODO type " + t + 
      			 " on fuction named " + seg.getName());
    }
    return (I_Pipe) this;
	}
	
  @Override
  public I_Run<I> fork(Consumer<? super O> consumer) {
  	return fork(consumer, Optional.empty());
  }

  public I_Run<I> fork(Consumer<? super O> consumer, String name) {
    return fork(consumer, Optional.of(name));
  }
	
  public I_Run<I> fork(Consumer<? super O> consumer, Optional<String> nameOpt) {
    return then(consumer, nameOpt);
  }

	@Override
	public <B, R> I_Pipe<I, R> fork(Function<? super O, ? extends R> fun) {
		return fork(fun);
	}
	
	@Override
	public <B, R> I_Pipe<I, R> fork(Function<? super O, ? extends R> fun, String name) {
		return map(fun, Optional.of(Objects.requireNonNull(name)));
	}
	
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Optional<O> get(I i) {
    return new PipeProcessor(ar.get()).process(i);
  }

  public Optional<O> get(I... in) {
    return get(List.of(in));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<O> get(Collection<I> in) {
    Optional<O> r = null;
    PipeProcessor proc = new PipeProcessor(ar.get());
    r = proc.process(in);
    if (r != null) {
      return r;
    }
    return Optional.empty();
  }

  public <R> I_Pipe<I, R> then(Function<? super O, ? extends R> mapper) {
    return map(mapper);
  }

  @Override
  public I_Run<I> then(Consumer<? super O> consumer) {
     return then(consumer, Optional.empty());	
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
	public I_Run<I> then(Consumer<? super O> consumer, Optional<String> nameOpt) {
    AbstractSegment seg = ar.get();
    seg.checkMappable();
    SegmentType t = seg.getType();
    switch (t) {
      case head:
          ar.set(new LinkSegment((HeadSegment) seg, consumer, nameOpt));
        break;
      case tail:
      case link:
          LinkSegment tfSeg = (LinkSegment) seg;
          AbstractSegment oldHead = tfSeg.getHead();
          AbstractSegment oldTail = tfSeg.getTail();
          ar.set(new LinkSegment(oldHead, new LinkSegment(oldTail, consumer, nameOpt)));
        break;
      default:
        throw new IllegalStateException("TODO " + t);
    }
    return (I_Pipe) this;
  }

  public <R> I_Pipe<I, R> map(Function<? super O, ? extends R> mapper) {
    return map(mapper, Optional.empty());
  }

  public <R> I_Pipe<I, R> map(Function<? super O, ? extends R> mapper, String name) {
    return map(mapper, Optional.of(name));
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <R> I_Pipe<I, R> map(Function<? super O, ? extends R> mapper, 
  		Optional<String> nameOpt) {
    AbstractSegment seg = ar.get();
    seg.checkMappable();
    SegmentType t = seg.getType();
    switch (t) {
      case head:
          ar.set(new LinkSegment(seg, mapper, nameOpt));
        break;
      case tail:
      case link:
          LinkSegment tfSeg = (LinkSegment) seg;
          AbstractSegment oldHead = tfSeg.getHead();
          AbstractSegment oldTail = tfSeg.getTail();
          ar.set(new LinkSegment(oldHead, new LinkSegment(oldTail, mapper, nameOpt)));
        break;
      default:
        throw new IllegalStateException("TODO " + t);
    }
    return (I_Pipe) this;
  }

  @Override
  public <R> I_Pipe<I, R> reduce(BinaryOperator<O> bo) {
    throw new IllegalStateException("method not yet implemented");
    // combiner.
  }

  //@Override
	public <R> I_Pipe<I, R> reduce(O identity, BinaryOperator<O> bo) {
  	return reduce(PipeOptional.of(Objects.requireNonNull(identity)),
  			Objects.requireNonNull(bo), Optional.empty());
  }
  
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <R> I_Pipe<I, R> reduce(PipeOptional<O> identityOpt, BinaryOperator<O> bo, 
			Optional<String> nameOpt) {  
    AbstractSegment seg = ar.get();
    seg.checkMappable();
    SegmentType t = seg.getType();
    switch (t) {
      case head:
          ar.set(new LinkSegment(seg, identityOpt, bo, nameOpt));
        break;
      case tail:
      case link:
          LinkSegment tfSeg = (LinkSegment) seg;
          AbstractSegment oldHead = tfSeg.getHead();
          AbstractSegment oldTail = tfSeg.getTail();
          ar.set(new LinkSegment(oldHead, 
          		new LinkSegment(oldTail, identityOpt, bo, nameOpt)));
        break;
      default:
        throw new IllegalStateException("TODO " + t);
    }
    return (I_Pipe) this;
  }

  @Override
  public <R> I_Pipe<I, R> reduce(R identity, BiFunction<R, ? super O, R> accumulator, BinaryOperator<O> combiner) {
    throw new IllegalStateException("method not yet implemented");
  }

  public void supply(I in) {
    supply(List.of(in));
  }

  public void supply(I... in) {
    supply(List.of(in));
  }

  public void supply(Collection<I> in) {
  	PipeProcessor<I, O> proc = new PipeProcessor<>(ar.get());
    proc.process(in);
  }

	@Override
	public I_Run<I> then(Consumer<? super O> arg0, String name) {
		throw new IllegalStateException("TODO ");
	}

	@Override
	public <R> I_Pipe<I, R> then(Function<? super O, ? extends R> arg0, String name) {
		throw new IllegalStateException("TODO ");
	}

	@Override
  public I_Pipe<I, List<O>> toList() {
    return toList(Optional.empty());
  }

	@Override
  public I_Pipe<I, List<O>> toList(String name) {
    return toList(Optional.of(name));
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public I_Pipe<I, List<O>> toList(Optional<String> nameOpt) {
  	PipeOptional<O> pol = (PipeOptional) PipeOptional.of(new ArrayList());
  	BinaryOperator<O> bo = (l, r) -> { 
			if (PipeOptional.empty() != r) {
				((List) l).add(r); 
			}
			return l; 
		};
    reduce(pol, bo, nameOpt);
    return (I_Pipe) this;
  }
  
	@Override
  public I_Pipe<I, Set<O>> toSet() {
    return toSet(Optional.empty());
  }

	@Override
  public I_Pipe<I, Set<O>> toSet(String name) {
    return toSet(Optional.of(name));
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public I_Pipe<I, Set<O>> toSet(Optional<String> nameOpt) {
  	PipeOptional<O> poo = (PipeOptional) PipeOptional.of(new HashSet());
  	BinaryOperator<O> bo = (l, r) -> { 
			if (PipeOptional.empty() != r) {
				((Set) l).add(r); 
			}
			return l; 
		};
    reduce(poo, bo, nameOpt);
    return (I_Pipe) this;
  }
}
