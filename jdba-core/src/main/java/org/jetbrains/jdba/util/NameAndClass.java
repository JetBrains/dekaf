package org.jetbrains.jdba.util;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class NameAndClass
{
  @NotNull
  public final String name;

  @NotNull
  public final Class clazz;


  public NameAndClass(@NotNull final String name, @NotNull final Class clazz) {
    this.name = name;
    this.clazz = clazz;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NameAndClass that = (NameAndClass) o;

    return name.equals(that.name) && clazz.equals(that.clazz);
  }

  @Override
  public int hashCode() {
    return name.hashCode() ^ clazz.hashCode();
  }

  @Override
  public String toString() {
    return name + ':' + clazz.getSimpleName();
  }

}
