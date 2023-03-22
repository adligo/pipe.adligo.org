package org.adligo.pipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.adligo.i_pipe.I_Pipe;
import org.adligo.i_pipe.I_Consumer;
import org.adligo.i_pipe.I_Distinguisher;

public class Pipe<I, O> implements I_Pipe<I, O>
//implements I_Pipe<T> 
{
  public static List<Integer> listOf(int ... i) {
		List<Integer> r = new ArrayList<>();
		for (int j = 0; j < i.length; j++) {
			r.add(i[j]);
		}  
		return r;
	  }
  public static <I> List<I> listOf(I ... i) {
		List<I> r = new ArrayList<>();
		for (int j = 0; j < i.length; j++) {
			r.add(i[j]);
		}  
		return r;
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

  public <B> Pipe(Function<I, B> head, Optional<String> headNameOpt, Function<B, O> tail,
      Optional<String> tailNameOpt) {
    ar.set(new LinkSegment(head, headNameOpt, tail, tailNameOpt));
  }

  public <B> Pipe(Function<I, B> head, Consumer<B> tail) {
    ar.set(new LinkSegment(head, tail));
  }

  public <B> Pipe(Function<I, B> head, Optional<String> headNameOpt, Consumer<B> tail, Optional<String> tailNameOpt) {
    ar.set(new LinkSegment(head, headNameOpt, tail, tailNameOpt));
  }
  
  @SuppressWarnings("unchecked")
public void accept(I in) {
    supply(listOf(in));
  }

  @SuppressWarnings("unchecked")
public void accept(I... in) {
    supply(listOf(in));
  }

  public void accept(Collection<I> in) {
    PipeProcessor<I, O> proc = new PipeProcessor<>(ar.get());
    proc.process(in);
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
      ar.set(new LinkSegment(oldHead, new LinkSegment(oldTail, new DistinctSegment(nameOpt))));
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
      ar.set(new LinkSegment(ls.getHead(), new LinkSegment(ls.getTail(), fh.build(predicate), nameOpt)));
      break;
    // intentional fails
    case headBi:
    case headConsumer:
      throw new IllegalStateException(
          String.format("Invalid type %s " + " on fuction named %s at this locaton.", "" + t, seg.getName()));
    default:
      throw new IllegalStateException("TODO type " + t + " on fuction named " + seg.getName());
    }
    return (I_Pipe) this;
  }

  @Override
  public I_Consumer<I> fork(Consumer<? super O> consumer) {
    return fork(consumer, Optional.empty());
  }

  public I_Consumer<I> fork(Consumer<? super O> consumer, String name) {
    return fork(consumer, Optional.of(name));
  }

  public I_Consumer<I> fork(Consumer<? super O> consumer, Optional<String> nameOpt) {
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
  public Optional<O> supply(I i) {
    return new PipeProcessor(ar.get()).process(i);
  }

  @SuppressWarnings("unchecked")
public Optional<O> supply(I... in) {
    return supply(listOf(in));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Optional<O> supply(Collection<I> in) {
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
  public I_Consumer<I> then(Consumer<? super O> consumer) {
    return then(consumer, Optional.empty());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public I_Consumer<I> then(Consumer<? super O> consumer, Optional<String> nameOpt) {
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
  public <R> I_Pipe<I, R> map(Function<? super O, ? extends R> mapper, Optional<String> nameOpt) {
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

  // @Override
  public <R> I_Pipe<I, R> reduce(O identity, BinaryOperator<O> bo) {
    return reduce(PipeOptional.of(Objects.requireNonNull(identity)), Objects.requireNonNull(bo), Optional.empty());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <R> I_Pipe<I, R> reduce(PipeOptional<O> identityOpt, BinaryOperator<O> bo, Optional<String> nameOpt) {
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
      ar.set(new LinkSegment(oldHead, new LinkSegment(oldTail, identityOpt, bo, nameOpt)));
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

  @Override
  public I_Consumer<I> then(Consumer<? super O> arg0, String name) {
    throw new IllegalStateException("TODO ");
  }

  @Override
  public <R> I_Pipe<I, R> then(Function<? super O, ? extends R> arg0, String name) {
    throw new IllegalStateException("TODO ");
  }

  @Override
  public List<O> toList() {
    throw new IllegalStateException("TODO");
  }

  @Override
  public List<O> toList(String name) {
    throw new IllegalStateException("TODO");
  }

  public List<O> toList(Optional<String> nameOpt) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, O> block(Predicate<? super O> predicate) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, O> block(Predicate<? super O> predicate, String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, O> distinct(I_Distinguisher<I> distinguisher) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, O> distinct(I_Distinguisher<I> distinguisher, String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public List<O> toCollection(Supplier<Collection<O>> collectionSupplier) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public List<O> toCollection(Supplier<Collection<O>> collectionSupplier, String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public List<O> toList(Supplier<List<O>> listSupplier) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public List<O> toList(Supplier<List<O>> listSupplier, String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public <K, V> Map<K, V> toMap(BiConsumer<O, Map<K, V>> collector) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public <K, V> Map<K, V> toMap(String name, BiConsumer<O, Map<K, V>> collector) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public <K, V> Map<K, V> toMap(Supplier<Map<K, V>> setSupplier, BiConsumer<O, Map<K, V>> collector) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public <K, V> Map<K, V> toMap(Supplier<Map<K, V>> setSupplier, String name, BiConsumer<O, Map<K, V>> collector) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public Set<O> toSet() {
    throw new IllegalStateException("TODO");
  }

  @Override
  public Set<O> toSet(String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public Set<O> toSet(Supplier<Set<O>> setSupplier) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public Set<O> toSet(Supplier<Set<O>> setSupplier, String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, Collection<O>> collection(Supplier<Collection<O>> collectionSupplier) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, Collection<O>> collection(Supplier<Collection<O>> collectionSupplier, String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, List<O>> list() {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, List<O>> list(String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, List<O>> list(Supplier<List<O>> listSupplier) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, List<O>> list(Supplier<List<O>> listSupplier, String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public <K, V> I_Pipe<I, Map<K, V>> map(BiConsumer<O, Map<K, V>> collector) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public <K, V> I_Pipe<I, Map<K, V>> map(String name, BiConsumer<O, Map<K, V>> collector) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public <K, V> I_Pipe<I, Map<K, V>> map(Supplier<Map<K, V>> setSupplier, BiConsumer<O, Map<K, V>> collector) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public <K, V> I_Pipe<I, Map<K, V>> map(Supplier<Map<K, V>> setSupplier, String name,
      BiConsumer<O, Map<K, V>> collector) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, Set<O>> set() {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, Set<O>> set(String name) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, Set<O>> set(Supplier<Set<O>> setSupplier) {
    throw new IllegalStateException("TODO");
  }

  @Override
  public I_Pipe<I, Set<O>> set(Supplier<Set<O>> setSupplier, String name) {
    throw new IllegalStateException("TODO");
  }

}
