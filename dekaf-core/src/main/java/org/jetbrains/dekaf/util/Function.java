package org.jetbrains.dekaf.util;

/**
 * Function of one argument.
 *
 * <p>
 *   Like this one: https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html
 * </p>
 *
 * @param <T>   type of the argument.
 * @param <R>   type of the result.
 *
 * @author Leonid Bushuev from JetBrains
 **/
public interface Function<T, R> {

  /**
   * The function itself.
   * @param arg   argument.
   * @return      result.
   */
  R apply(T arg);

}
