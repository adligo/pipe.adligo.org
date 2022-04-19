package org.adligo.pipe;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.adligo.i_pipe.I_Pipe;
import org.adligo.i_pipe.I_PipeCtx;
import org.adligo.i_pipe.I_Consumer;

/**
 * A Context class that facilitates creation of I_Pipes,
 * which SHOULD be used to facilitate simpler testint with Mockito
 * @see {@link https://github.com/adligo/mockito_ext.adligo.org/blob/main/src/org/adligo/mockito_ext/I_Mockito.java
 * @author scott
 *
 */
public class PipeCtx implements I_PipeCtx {

  public <I> I_Pipe<I, Void> newPipe(Consumer<I> consumer) {
    return new Pipe<I, Void>(consumer);
  }

  public  <I> I_Pipe<I, Void> newPipe(Consumer<I> consumer, String name) {
    return new Pipe<I, Void>(consumer, Optional.of(Objects.requireNonNull(name)));
  }

  public <I, O> I_Pipe<I, O> newPipe(Function<I,O> fun) {
    return new Pipe<I, O>(fun);
  }

  public <I, O> I_Pipe<I, O> newPipe(Function<I, O> fun, String name) {
    return new Pipe<I, O>(fun, Optional.of(Objects.requireNonNull(name)));
  }
}
