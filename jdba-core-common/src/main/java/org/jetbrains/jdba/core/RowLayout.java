package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.util.NameAndClass;

import java.io.Serializable;
import java.util.Arrays;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class RowLayout<R> implements Serializable {

  public enum Kind {
    EXISTENCE,
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
  public final NameAndClass[] components;

  private final boolean portable;


  RowLayout(@NotNull final Kind kind,
            @NotNull final Class<R> rowClass,
            @NotNull final Class commonComponentClass,
            @NotNull final NameAndClass[] components) {
    this.kind = kind;
    this.rowClass = rowClass;
    this.commonComponentClass = commonComponentClass;

    this.components = components;

    // portability
    switch (kind) {
      case EXISTENCE:
      case ONE_VALUE:
      case ARRAY:
        portable = true;
        break;
      case CLASS_BY_NAMES:
      case CLASS_BY_POSITIONS:
        String className = rowClass.getName();
        portable = className.startsWith("java.") || className.startsWith("javax.");
        break;
      default:
        portable = false;
    }
  }

  RowLayout(@NotNull final Kind kind,
            @NotNull final Class<R> rowClass,
            @NotNull final Class commonComponentClass,
            @NotNull final Class... componentClasses) {
    this(kind, rowClass, commonComponentClass, unnamedComponentsOf(componentClasses));
  }

  @NotNull
  private static NameAndClass[] unnamedComponentsOf(final @NotNull Class[] componentClasses) {
    final int n = componentClasses.length;
    final NameAndClass[] components = new NameAndClass[n];
    for (int i = 0; i < n; i++) components[i] =
                                        new NameAndClass('#'+Integer.toString(i+1), componentClasses[i]);
    return components;
  }


  public boolean isPortable() {
    return portable;
  }


  public RowLayout<Object[]> makeIntermediateLayout() {
    return new RowLayout<Object[]>(Kind.ARRAY, Object[].class, commonComponentClass, components);
  }


  //// LEGACY METHODS \\\\

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RowLayout<?> rowLayout = (RowLayout<?>) o;

    if (kind != rowLayout.kind) return false;
    if (!rowClass.equals(rowLayout.rowClass)) return false;
    if (!commonComponentClass.equals(rowLayout.commonComponentClass)) return false;
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(components, rowLayout.components);

  }

  @Override
  public int hashCode() {
    int result = kind.hashCode();
    result = 31 * result + rowClass.hashCode();
    result = 31 * result + commonComponentClass.hashCode();
    result = 31 * result + Arrays.hashCode(components);
    return result;
  }


  @Override
  public String toString() {
    switch (kind) {
      case EXISTENCE:
        return "existence";
      case ONE_VALUE:
        return commonComponentClass.getSimpleName();
      case ARRAY:
        return commonComponentClass.getSimpleName() + "[]";
      case CLASS_BY_NAMES:
        return rowClass + "(fields names)";
      case CLASS_BY_POSITIONS:
        return rowClass + "(positions)";
      default:
        return "???";
    }
  }
}
