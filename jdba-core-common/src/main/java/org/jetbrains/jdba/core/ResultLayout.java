package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class ResultLayout<T> implements Serializable {

  //// INNER DECLARATIONS \\\\

  public enum Kind {
    EXISTENCE,
    SINGLE_ROW,
    ARRAY,
    ARRAY_OF_PRIMITIVES,
    LIST,
    SET,
    MAP
  }

  //// STATE \\\\

  @NotNull
  public final Kind kind;

  public final boolean sorted;

  @NotNull
  public final RowLayout<?> row;

  public final int initialCapacity;


  //// CONSTRUCTORS \\\\

  ResultLayout(@NotNull final Kind kind, final boolean sorted, @NotNull RowLayout row) {
    this.kind = kind;
    this.sorted = sorted;
    this.row = row;
    this.initialCapacity = 1024;
  }

  ResultLayout(@NotNull final Kind kind,
               final boolean sorted,
               @NotNull final RowLayout<?> row,
               int initialCapacity) {
    this.kind = kind;
    this.sorted = sorted;
    this.row = row;
    this.initialCapacity = initialCapacity;
  }


  //// PORTABILITY METHODS \\\\

  public boolean isPortable() {
    return row.isPortable();
  }

  public ResultLayout<List<Object[]>> makeIntermediateLayout() {
    return new ResultLayout<List<Object[]>>(Kind.LIST, false, row.makeIntermediateLayout());
  }


  //// LEGACY METHODS \\\\


  @Override
  public String toString() {
    switch (kind) {
      case EXISTENCE:
        return "existence";
      default:
        return kind.name() + " of " + row.toString();
    }
  }

}
