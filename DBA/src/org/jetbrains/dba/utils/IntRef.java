package org.jetbrains.dba.utils;

import java.io.Serializable;



/**
 * An integer holder.
 *
 * @author Leonid Bushuev from JetBrains
 */
public final class IntRef
  implements Serializable, Cloneable {
  public int value;


  public IntRef() {
  }


  public IntRef(final int value) {
    this.value = value;
  }


  @Override
  public int hashCode() {
    return value;
  }


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final IntRef intRef = (IntRef)o;

    if (value != intRef.value) return false;

    return true;
  }


  @Override
  protected Object clone() {
    return new IntRef(value);
  }


  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
