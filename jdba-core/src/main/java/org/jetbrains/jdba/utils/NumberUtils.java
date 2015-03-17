package org.jetbrains.jdba.utils;

import org.jetbrains.annotations.Nullable;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class NumberUtils {

  public static int parseIntSafe(@Nullable final String str) {
    if (str == null) {
      return 0;
    }
    try {
      final String s = str.trim();
      if (s.isEmpty()) return 0;
      if (s.charAt(0) == '+') return Integer.parseInt(s.substring(1));
      return Integer.parseInt(s);
    }
    catch (NumberFormatException e) {
      return 0;
    }
  }


  public static long parseLongSafe(@Nullable final String str) {
    if (str == null) {
      return 0;
    }
    try {
      final String s = str.trim();
      if (s.isEmpty()) return 0L;
      if (s.charAt(0) == '+') return Long.parseLong(s.substring(1));
      return Long.parseLong(s);
    }
    catch (NumberFormatException e) {
      return 0;
    }
  }
}
