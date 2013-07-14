package org.jetbrains.dba.utils;

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
      return Integer.parseInt(str.trim());
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
      return Long.parseLong(str.trim());
    }
    catch (NumberFormatException e) {
      return 0;
    }
  }
}
