package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class ColumnBriefInfo {

  @NotNull
  public final String name;

  @NotNull
  public final String nativeType;

  public final int jdbcType;

  ColumnBriefInfo(@NotNull final String name, @NotNull final String nativeType, final int jdbcType) {
    this.name = name;
    this.nativeType = nativeType;
    this.jdbcType = jdbcType;
  }

  @Override
  public String toString() {
    return name + ':' + nativeType + ':' + Integer.toString(jdbcType);
  }


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ColumnBriefInfo that = (ColumnBriefInfo) o;

    return jdbcType == that.jdbcType
                   && name.equals(that.name)
                   && nativeType.equals(that.nativeType);
  }

  @Override
  public int hashCode() {
    return name.hashCode() * 19 + nativeType.hashCode() * 13 + Math.abs(jdbcType);
  }
}
