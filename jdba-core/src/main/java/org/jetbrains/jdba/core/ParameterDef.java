package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;



/**
 * Parameter definition.
 *
 * @author Leonid Bushuev from JetBrains
 */
public final class ParameterDef implements Serializable {

  @NotNull
  public final Class valueType;

  public final boolean useNChar;


  public ParameterDef(@NotNull final Class valueType) {
    this.valueType = valueType;
    this.useNChar = false;
  }

  public ParameterDef(@NotNull final Class valueType, final boolean useNChar) {
    this.valueType = valueType;
    this.useNChar = useNChar;
  }


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ParameterDef that = (ParameterDef) o;

    if (useNChar != that.useNChar) return false;
    return valueType.equals(that.valueType);

  }

  @Override
  public int hashCode() {
    return valueType.hashCode();
  }
}
