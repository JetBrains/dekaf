package org.jetbrains.dba.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;



/**
 * Useful string functions.
 *
 * @author Leonid Bushuev from JetBrains
 */
public final class Strings {

  public static boolean eq(final String str1, final String str2) {
    if (str1 == str2) return true;
    if (str1 == null || str2 == null) return false;
    return str1.equals(str2);
  }


  @NotNull
  public static String ensureStartsWith(@NotNull final String str, final char c) {
    if (str.length() == 0) {
      return Character.toString(c);
    }
    else {
      char c1 = str.charAt(0);
      if (c1 == c) return str;
      else return c + str;
    }
  }


  @NotNull
  public static String ensureEndsWith(@NotNull final String str, final char c) {
    final int n = str.length();
    if (n > 0 && str.charAt(n - 1) == c) {
      return str;
    }
    else {
      return str + c;
    }
  }


  public static void ensureEndsWith(@NotNull final StringBuilder stringBuilder, final char c) {
    final int n = stringBuilder.length();
    if (n == 0 || stringBuilder.charAt(n - 1) != c) stringBuilder.append(c);
  }


  @NotNull
  public static String removeEnding(@NotNull final String str, @NotNull final String ending) {
    int n = str.length(),
        m = ending.length();
    if (n > m && str.endsWith(ending)) return str.substring(0, n-m);
    else return str;
  }


  @NotNull
  public static String rtrim(@NotNull final String str) {
    int n = str.length();
    int k = n;
    while (k > 0) {
      if (Character.isWhitespace(str.charAt(k - 1))) {
        k--;
      }
      else {
        break;
      }
    }

    if (k == n) {
      return str;
    }
    else {
      return str.substring(0, k);
    }
  }


  public static boolean matches(@Nullable final CharSequence string, @NotNull final Pattern pattern) {
    if (string == null) return false;
    return pattern.matcher(string).matches();
  }
}
