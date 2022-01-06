package org.adligo.pipe;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.adligo.i.pipe.I_Pipe;
import org.adligo.i.pipe.I_PipeCtx;
import org.adligo.i.pipe.I_Run;

/**
 * A Context class that facilitates creation of I_Pipes,
 * which SHOULD be used to facilitate simpler testint with Mockito
 * @see {@link https://github.com/adligo/mockito_ext.adligo.org/blob/main/src/org/adligo/mockito_ext/I_Mockito.java
 * @author scott
 *
 */
public class PipeCtx implements I_PipeCtx {

  public <PI, PO> I_Pipe<PI, PO> newPipe(Consumer<PI> consumer) {
    return new Pipe<PI, PO>(consumer);
  }

  public <PI, PO> I_Pipe<PI, PO> newPipe(Consumer<PI> consumer, String name) {
    return new Pipe<PI, PO>(consumer, Optional.of(Objects.requireNonNull(name)));
  }
  
  public <PI> I_Run<PI> newPipe(Class<PI> inClazz, Consumer<PI> consumer) {
    return new Pipe<PI, Void>(consumer);
  }

  public <PI> I_Run<PI> newPipe(Class<PI> inClazz, 
  		Consumer<PI> consumer, String name) {
    return new Pipe<PI, Void>(consumer, Optional.of(Objects.requireNonNull(name)));
  }
  
  public <PI, PO> I_Pipe<PI, PO> newPipe(Function<PI, PO> fun) {
    return new Pipe<PI, PO>(fun);
  }

  public <PI, PO> I_Pipe<PI, PO> newPipe(Function<PI, PO> fun, String name) {
    return new Pipe<PI, PO>(fun, Optional.of(Objects.requireNonNull(name)));
  }
  
  public <PI, PO> I_Pipe<PI, PO> newPipe(Class<PI> inClazz, Class<PO> outClazz, Function<PI, PO> fun) {
    return new Pipe<PI, PO>(fun);
  }

  public <PI, PO> I_Pipe<PI, PO> newPipe(Class<PI> inClazz, Class<PO> outClazz, 
  		Function<PI, PO> fun, String name) {
    return new Pipe<PI, PO>(fun, Optional.of(Objects.requireNonNull(name)));
  }
}
