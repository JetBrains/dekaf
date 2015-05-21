package org.jetbrains.jdba.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;



/**
 * Some usable array functions.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class ArrayFunctions {

  @NotNull
  public static String arrayToString(final Object[] array, final String delimiter) {
    if (array == null) return "";
    int n = array.length;
    switch (n) {
      case 0:
        return "";
      case 1:
        return array[0].toString();
      default:
        StringBuilder b = new StringBuilder();
        b.append(array[0].toString());
        for (int i = 1; i < n; i++) b.append(delimiter).append(array[i]);
        return b.toString();
    }
  }


  @NotNull
  public static <T> T[][] splitArrayPer(@NotNull final T[] array, final int limitPerPack) {
    final int n = array.length;
    final Class<? extends Object[]> arrayType = array.getClass();

    int m = n / limitPerPack,
        r = n % limitPerPack;
    if (r > 0) m++;

    //noinspection unchecked
    T[][] packs = (T[][]) Array.newInstance(arrayType, m);

    int k = 0;
    for (int i = 0; i < m && k < n; i++, k+=limitPerPack) {
      int end = Math.min(k + limitPerPack, n);
      packs[i] = Arrays.copyOfRange(array, k, end);
    }

    return packs;
  }


}
