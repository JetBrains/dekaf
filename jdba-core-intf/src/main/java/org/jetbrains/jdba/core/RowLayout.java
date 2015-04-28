package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class RowLayout<R> implements Serializable {

  public final boolean portable;

  @NotNull
  public final Class<R> rowClass;

  @NotNull
  public final Class[] componentClasses;


  RowLayout(final boolean portable,
            @NotNull final Class<R> rowClass,
            @NotNull final Class... componentClasses) {
    this.portable = portable;
    this.rowClass = rowClass;
    this.componentClasses = componentClasses;
  }


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RowLayout rowLayout = (RowLayout) o;

    if (portable != rowLayout.portable) return false;
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(componentClasses, rowLayout.componentClasses);

  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(componentClasses);
  }
}
