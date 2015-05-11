package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.util.Classes;
import org.jetbrains.jdba.util.NameAndClass;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class Layouts {


  //// ROW LAYOUTS \\\\

  @NotNull
  public static <V> RowLayout<V> oneOf(@NotNull final Class<V> valueClass) {
    return new RowLayout<V>(RowLayout.Kind.ONE_VALUE, valueClass, valueClass, valueClass);
  }

  @SuppressWarnings("unchecked")
  @NotNull
  public static <V> RowLayout<V[]> arrayOf(int n, @NotNull final Class<V> componentClass) {
    V[] example = (V[]) Array.newInstance(componentClass,0);
    Class resultClass = example.getClass();
    Class<V>[] componentClasses = (Class<V>[]) new Class[n];
    for (int i = 0; i < n; i++) componentClasses[i] = componentClass;
    return new RowLayout<V[]>(RowLayout.Kind.ARRAY, resultClass, componentClass, componentClasses);
  }

  @NotNull
  public static RowLayout<Object[]> arrayOf(@NotNull final Class... componentClasses) {
    return new RowLayout<Object[]>(RowLayout.Kind.ARRAY, Object[].class, Object.class, componentClasses);
  }

  @NotNull
  public static <C> RowLayout<C> structOf(@NotNull final Class<C> structClass) {
    final NameAndClass[] assignableFields = Classes.assignableFieldsOf(structClass);
    return new RowLayout<C>(RowLayout.Kind.CLASS_BY_NAMES, structClass, Object.class, assignableFields);
  }


  //// RESULT LAYOUTS \\\\

  public static <V> ResultLayout<V> singleOf(@NotNull final Class<V> valueClass) {
    return new ResultLayout<V>(ResultLayout.Kind.SINGLE_ROW, false, oneOf(valueClass));
  }

  public static <R> ResultLayout<R> rowOf(@NotNull final RowLayout<R> rowLayout) {
    return new ResultLayout<R>(ResultLayout.Kind.SINGLE_ROW, false, rowLayout);
  }

  public static <V> ResultLayout<V[]> columnOf(@NotNull final Class<V> valueClass) {
    return new ResultLayout<V[]>(ResultLayout.Kind.ARRAY, false, oneOf(valueClass));
  }

  public static ResultLayout<int[]> columnOfInts(int initialCapacity) {
    return new ResultLayout<int[]>(ResultLayout.Kind.ARRAY_OF_PRIMITIVES, false, oneOf(int.class), initialCapacity);
  }

  public static ResultLayout<long[]> columnOfLongs(int initialCapacity) {
    return new ResultLayout<long[]>(ResultLayout.Kind.ARRAY_OF_PRIMITIVES, false, oneOf(long.class), initialCapacity);
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

  public static <K extends Comparable<K>, V> ResultLayout<SortedMap<K,V>> sortedMapOf(@NotNull final Class<K> keyClass, @NotNull final Class<V> valueClass) {
    return new ResultLayout<SortedMap<K,V>>(ResultLayout.Kind.MAP, true, Layouts.arrayOf(keyClass, valueClass));
  }


}
