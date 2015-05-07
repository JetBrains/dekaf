package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class RowLayout<R> implements Serializable {

  public enum Kind {
    ONE_VALUE,
    ARRAY,
    CLASS_BY_POSITIONS,
    CLASS_BY_NAMES
  }

  @NotNull
  public final Kind kind;

  @NotNull
  public final Class<R> rowClass;

  @NotNull
  public final Class commonComponentClass;

  @NotNull
  public final Class[] componentClasses;


  RowLayout(@NotNull final Kind kind,
            @NotNull final Class<R> rowClass,
            @NotNull final Class commonComponentClass,
            @NotNull final Class... componentClasses) {
    this.kind = kind;
    this.rowClass = rowClass;
    this.commonComponentClass = commonComponentClass;
    this.componentClasses = componentClasses;
  }



  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RowLayout<?> rowLayout = (RowLayout<?>) o;

    if (kind != rowLayout.kind) return false;
    if (!rowClass.equals(rowLayout.rowClass)) return false;
    if (!commonComponentClass.equals(rowLayout.commonComponentClass)) return false;
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(componentClasses, rowLayout.componentClasses);

  }

  @Override
  public int hashCode() {
    int result = kind.hashCode();
    result = 31 * result + rowClass.hashCode();
    result = 31 * result + commonComponentClass.hashCode();
    result = 31 * result + Arrays.hashCode(componentClasses);
    return result;
  }
}
