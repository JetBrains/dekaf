package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

import static java.lang.String.format;



/**
 * Useful functions for working with objects.
 *
 * @author Leonid Bushuev from JetBrains
 **/
public abstract class Objects {


  @NotNull @SuppressWarnings("unchecked")
  public static <E> E[] arrayOf(@NotNull final Class<E> elementClass, int length) {
    return (E[]) Array.newInstance(elementClass, length);
  }

  @Contract("_,!null->!null; _,null->null") @SuppressWarnings("unchecked")
  public static <C> C castTo(@NotNull final Class<C> clazz, @Nullable final Object object)
      throws ClassCastException
  {
    if (object != null) {
      final Class<?> objectClass = object.getClass();
      if (clazz.isAssignableFrom(objectClass)) {
        return (C) object;
      }
      else {
        String kind = clazz.isInterface() ? "interface" : "class";
        String msg = format("cannot cast object of class %s to %s %s.",
                            objectClass.getName(), kind, clazz.getName());
        throw new ClassCastException(msg);
      }
    }
    else {
      return null;
    }
  }


  @NotNull @SuppressWarnings("unchecked")
  public static <E> E[] castToArrayOf(@NotNull final Class<E> elementClass, @NotNull final Object object)
      throws ClassCastException
  {
    Class<?> objectClass = object.getClass();
    if (objectClass.isArray()) {
      // should we check every element?
      return (E[]) object;
    }
    else {
      throw new ClassCastException("Atemmpted to cast a "+objectClass.getSimpleName()+" to an array of "+elementClass.getSimpleName());
    }
  }


  @NotNull
  public static <T> T notNull(@Nullable final T value, @NotNull final T defaultValue) {
    return value != null ? value : defaultValue;
  }

}
