package org.adligo.pipe;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceExt<V> extends AtomicReference<V> {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * I want to combine AgomicReference and Optional :)
   * @return
   */
  public boolean has() {
    V o = super.get();
    if (o == null) {
      return false;
    } 
    return true;
  }
}
