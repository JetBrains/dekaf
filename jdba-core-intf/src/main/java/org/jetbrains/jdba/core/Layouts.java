package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class Layouts {


  //// ROW LAYOUTS \\\\

  @NotNull
  public static <V> RowLayout<V> oneOf(@NotNull final Class<V> valueClass) {
    return new RowLayout<V>(true, valueClass, valueClass);
  }

  @SuppressWarnings("unchecked")
  @NotNull
  public static <C> RowLayout<C[]> arrayOf(int n, @NotNull final Class<C> componentClass) {
    C[] example = (C[]) Array.newInstance(componentClass,0);
    Class resultClass = example.getClass();
    Class<C>[] componentClasses = (Class<C>[]) new Class[n];
    for (int i = 0; i < n; i++) componentClasses[i] = componentClass;
    return new RowLayout<C[]>(true, resultClass, componentClasses);
  }

  @NotNull
  public static RowLayout<Object[]> arrayOf(@NotNull final Class... componentClasses) {
    return new RowLayout<Object[]>(true, Object[].class, componentClasses);
  }


  //// RESULT LAYOUTS \\\\

  public static <V> ResultLayout<V> singleOf(@NotNull final Class<V> valueClass) {
    return new ResultLayout<V>(ResultLayout.Kind.SINGLE, false, oneOf(valueClass));
  }

  public static <R> ResultLayout<R> rowOf(@NotNull final RowLayout<R> rowLayout) {
    return new ResultLayout<R>(ResultLayout.Kind.SINGLE, false, rowLayout);
  }

  public static <R> ResultLayout<R[]> arrayOf(@NotNull final RowLayout<R> rowLayout) {
    return new ResultLayout<R[]>(ResultLayout.Kind.ARRAY, false, rowLayout);
  }

  public static <R> ResultLayout<List<R>> listOf(@NotNull final RowLayout<R> rowLayout) {
    return new ResultLayout<List<R>>(ResultLayout.Kind.LIST, false, rowLayout);
  }

  public static <K,V> ResultLayout<Map<K,V>> hashMapOf(@NotNull final Class<K> keyClass, @NotNull final Class<V> valueClass) {
    return new ResultLayout<Map<K,V>>(ResultLayout.Kind.MAP, false, Layouts.arrayOf(keyClass, valueClass));
  }


}
