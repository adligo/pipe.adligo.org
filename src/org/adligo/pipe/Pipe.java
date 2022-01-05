package org.adligo.pipe;

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

  public static final <PI, PO> I_Pipe<PI, PO> of(Consumer<PI> consumer) {
    return new Pipe<PI, PO>(consumer);
  }

  public static final <PI, PO> I_Pipe<PI, PO> of(Consumer<PI> consumer, String name) {
    return new Pipe<PI, PO>(consumer, Optional.of(Objects.requireNonNull(name)));
  }
  
  public static final <PI> I_Run<PI> of(Class<PI> inClazz, Consumer<PI> consumer) {
    return new Pipe<PI, Void>(consumer);
  }

  public static final <PI> I_Run<PI> of(Class<PI> inClazz, 
  		Consumer<PI> consumer, String name) {
    return new Pipe<PI, Void>(consumer, Optional.of(Objects.requireNonNull(name)));
  }
  
  public static final <PI, PO> I_Pipe<PI, PO> of(Function<PI, PO> fun) {
    return new Pipe<PI, PO>(fun);
  }

  public static final <PI, PO> I_Pipe<PI, PO> of(Function<PI, PO> fun, String name) {
    return new Pipe<PI, PO>(fun, Optional.of(Objects.requireNonNull(name)));
  }
  
  public static final <PI, PO> I_Pipe<PI, PO> of(Class<PI> inClazz, Class<PO> outClazz, Function<PI, PO> fun) {
    return new Pipe<PI, PO>(fun);
  }

  public static final <PI, PO> I_Pipe<PI, PO> of(Class<PI> inClazz, Class<PO> outClazz, 
  		Function<PI, PO> fun, String name) {
    return new Pipe<PI, PO>(fun, Optional.of(Objects.requireNonNull(name)));
  }
  
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
  
  @Override
  public I_Run<I> decision(Consumer<? super O> consumer) {
  	return decision(consumer, Optional.empty());
  }

  public I_Run<I> decision(Consumer<? super O> consumer, String name) {
    return decision(consumer, Optional.of(name));
  }
	
  public I_Run<I> decision(Consumer<? super O> consumer, Optional<String> nameOpt) {
    return then(consumer, nameOpt);
  }

	@Override
	public <B, R> I_Pipe<I, R> decision(Function<? super O, ? extends R> fun) {
		return decision(fun);
	}
	
	@Override
	public <B, R> I_Pipe<I, R> decision(Function<? super O, ? extends R> fun, String name) {
		return map(fun, Optional.of(Objects.requireNonNull(name)));
	}
	
  public I_Pipe<I, O> distinct() {
    return map(new Function<O, O>() {
      Set<O> set = new HashSet<O>();

      @Override
      public O apply(O t) {
        if (set.contains(t)) {
          return null;
        } else {
          set.add(t);
          return t;
        }
      }

    });
  }

	@Override
	public I_Pipe<I, O> filter(Predicate<? super O> predicate) {
		return filter(predicate, Optional.empty());
	}
	
	@Override
	public I_Pipe<I, O> filter(Predicate<? super O> predicate, String name) {
		return filter(predicate, Optional.of(Objects.requireNonNull(name)));
	}

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
	

  @SuppressWarnings("unchecked")
  public Optional<O> get(I i) {
  	Set<AbstractSegment> segsStarted = new HashSet<>();
    return new PipeProcessor(ar.get()).process(segsStarted, i);
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

  @SuppressWarnings({ "unchecked", "rawtypes" })
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
          LinkSegment tSeg = (LinkSegment) seg;
          AbstractSegment tOldHead = tSeg.getHead();
          AbstractSegment tOldTail = tSeg.getTail();
          ar.set(new LinkSegment(tOldHead, new LinkSegment(tOldTail, consumer, nameOpt)));
        break;
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
          LinkSegment tSeg = (LinkSegment) seg;
          AbstractSegment tOldHead = tSeg.getHead();
          AbstractSegment tOldTail = tSeg.getTail();
          ar.set(new LinkSegment(tOldHead, new LinkSegment(tOldTail, mapper, nameOpt)));
        break;
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
          LinkSegment tSeg = (LinkSegment) seg;
          AbstractSegment tOldHead = tSeg.getHead();
          AbstractSegment tOldTail = tSeg.getTail();
          ar.set(new LinkSegment(tOldHead, 
          		new LinkSegment(tOldTail, identityOpt, bo, nameOpt)));
        break;
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
    throw new IllegalStateException("Method not yet implemented");
    // reduce( ,new ArrayList<>, Collectors.toList());
  }

  /*
   * public I_Pipe<I,List<O>> toList(Supplier<List<O>> listSupplier) { return
   * map(new Function<O,List<O>>() {
   * 
   * @Override public List<O> apply(O t) { listSupplier.get() list.add(t); return
   * list; }
   * 
   * public List<O> apply(O t, List<O> list) { list.add(t); return list; } }); }
   * 
   * @Override public I_Pipe<I,Set<O>> toSet() { return map(new
   * Function<O,Set<O>>() { Set<O> r = new HashSet<>();
   * 
   * @Override public Set<O> apply(O t) { r.add(t); return r; } }); }
   */
}
