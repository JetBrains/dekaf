package org.jetbrains.jdba.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.String.format;



/**
 * Useful functions for working with objects.
 *
 * @author Leonid Bushuev from JetBrains
 **/
public abstract class Objects {

  @Contract("_,!null->!null; _,null->null")
  public static <C> C castTo(@NotNull final Class<C> clazz, @Nullable final Object object)
      throws ClassCastException
  {
    if (object != null) {
      final Class<?> objectClass = object.getClass();
      if (clazz.isAssignableFrom(objectClass)) {
        //noinspection unchecked
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

}
