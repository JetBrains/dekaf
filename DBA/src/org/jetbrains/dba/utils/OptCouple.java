package org.jetbrains.dba.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;



/**
 * An 'optional' couple - a couple of nullable objects.
 *
 * @author Leonid Bushuev from JetBrains
 * @see Couple
 */
public final class OptCouple<T> implements Serializable {

  @Nullable
  public final T a;

  @Nullable
  public final T b;


  public OptCouple(@Nullable final T a, @Nullable final T b) {
    this.a = a;
    this.b = b;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OptCouple couple = (OptCouple)o;

    if (a != null ? !a.equals(couple.a) : couple.a != null) return false;
    if (b != null ? !b.equals(couple.b) : couple.b != null) return false;

    return true;
  }

  public boolean isEmpty() {
    return a == null && b == null;
  }

  public boolean isPartial() {
    return (a == null) ^ (b == null);
  }

  public boolean isFull() {
    return a != null && b != null;
  }

  @Override
  public int hashCode() {
    return (a == null ? 0 : a.hashCode() * 17) ^ (a == null ? 0 : a.hashCode() * 13);
  }


  @Override
  public String toString() {
    return "(" + (a == null ? "null" : a.toString()) + "|" + (b == null ? "null" : b.hashCode()) + ")";
  }
}
