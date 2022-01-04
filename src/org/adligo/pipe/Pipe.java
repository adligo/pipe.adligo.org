package org.adligo.pipe;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

  public static final <PI> I_Run<PI> of(Class<PI> inClazz, Consumer<PI> consumer) {
    return new Pipe<PI, Void>(consumer);
  }

  public static final <PI, PO> I_Pipe<PI, PO> of(Function<PI, PO> fun) {
    return new Pipe<PI, PO>(fun);
  }

  public static final <PI, PO> I_Pipe<PI, PO> of(Class<PI> inClazz, Class<PO> outClazz, Function<PI, PO> fun) {
    return new Pipe<PI, PO>(fun);
  }

  private final AtomicReferenceExt<AbstractSegment> ar = new AtomicReferenceExt<>();

  public Pipe(Consumer<I> consumer) {
    ar.set(new HeadConsumerSegment<>(consumer));
  }

  public <B> Pipe(Function<I, B> head) {
    ar.set(new HeadSegment<>(head));
  }

  public <B> Pipe(Function<I, B> head, Function<B, O> tail) {
    ar.set(new LinkSegment(head, tail));
  }

  public <B> Pipe(Function<I, B> head, Consumer<B> tail) {
    ar.set(new LinkSegment(head, tail));
  }

  @Override
  public I_Run<I> decision(Consumer<? super O> consumer) {
    return then(consumer);
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
  public <B, R> I_Pipe<I, R> decision(Function<? super O, ? extends R> mapper) {
    return map(mapper);
  }

  @SuppressWarnings("unchecked")
  public Optional<O> get(I i) {
    return new PipeProcessor(ar.get()).process(i);
  }

  public Optional<O> get(I... in) {
    return get(List.of(in));
  }

  public Optional<O> get(Collection<I> in) {
    Optional<O> r = null;
    PipeProcessor proc = new PipeProcessor(ar.get());
    for (I i : in) {
      r = proc.process(i);
    }
    if (r != null) {
      return r;
    }
    return Optional.empty();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public I_Pipe<I, O> filter(Predicate<? super O> predicate) {

//    head.allowIfNotConsumer();
//    Function<O, O> d = (i) -> {
//      return i;
//    };
//    if (segment.isEmpty()) {
//      segment = Optional.of(new PipeSegment(head, d, predicate));
//      return (I_Pipe) this;
//    } else {
//      PipeSegment seg = segment.get();
//      PipeSegment newTail = new PipeSegment(seg.getTail(), d, predicate);
//      segment = Optional.of(new PipeSegment(head, newTail));
//    }
    return (I_Pipe) this;
  }

  public <R> I_Pipe<I, R> then(Function<? super O, ? extends R> mapper) {
    return map(mapper);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public I_Run<I> then(Consumer<? super O> consumer) {
    AbstractSegment seg = ar.get();
    seg.checkMappable();
    SegmentType t = seg.getType();
    switch (t) {
      case head:
          ar.set(new LinkSegment((HeadSegment) seg, consumer));
        break;
      case tail:
          LinkSegment tSeg = (LinkSegment) seg;
          AbstractSegment tOldHead = tSeg.getHead();
          AbstractSegment tOldTail = tSeg.getTail();
          ar.set(new LinkSegment(tOldHead, new LinkSegment(tOldTail, consumer)));
        break;
      case link:
          LinkSegment tfSeg = (LinkSegment) seg;
          AbstractSegment oldHead = tfSeg.getHead();
          AbstractSegment oldTail = tfSeg.getTail();
          ar.set(new LinkSegment(oldHead, new LinkSegment(oldTail, consumer)));
        break;
      default:
        throw new IllegalStateException("TODO " + t);
    }
    return (I_Pipe) this;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <R> I_Pipe<I, R> map(Function<? super O, ? extends R> mapper) {
    AbstractSegment seg = ar.get();
    seg.checkMappable();
    SegmentType t = seg.getType();
    switch (t) {
      case head:
          ar.set(new LinkSegment(seg, mapper));
        break;
      case tail:
          LinkSegment tSeg = (LinkSegment) seg;
          AbstractSegment tOldHead = tSeg.getHead();
          AbstractSegment tOldTail = tSeg.getTail();
          ar.set(new LinkSegment(tOldHead, new LinkSegment(tOldTail, mapper)));
        break;
      case link:
          LinkSegment tfSeg = (LinkSegment) seg;
          AbstractSegment oldHead = tfSeg.getHead();
          AbstractSegment oldTail = tfSeg.getTail();
          ar.set(new LinkSegment(oldHead, new LinkSegment(oldTail, mapper)));
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
    for (I i : in) {
      //apply(i);
    }
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
