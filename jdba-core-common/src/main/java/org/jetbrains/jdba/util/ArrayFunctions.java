package org.jetbrains.jdba.util;

import org.jetbrains.annotations.NotNull;



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

}
