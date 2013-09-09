package org.jetbrains.dba.utils;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;



/**
 * A couple of non-null objects of the same type.
 *
 * @author Leonid Bushuev from JetBrains
 * @see OptCouple
 */
public final class Couple<T> implements Serializable {

  @NotNull
  public final T a;

  @NotNull
  public final T b;


  public Couple(@NotNull final T a, @NotNull final T b) {
    this.a = a;
    this.b = b;
  }


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Couple couple = (Couple)o;

    if (!a.equals(couple.a)) return false;
    if (!b.equals(couple.b)) return false;

    return true;
  }


  @Override
  public int hashCode() {
    return a.hashCode() * 17 ^ a.hashCode() * 13;
  }


  @Override
  public String toString() {
    return "(" + a.toString() + "|" + b.hashCode() + ")";
  }
}
