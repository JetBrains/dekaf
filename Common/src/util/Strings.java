package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Useful string functions.
 *
 * @author Leonid Bushuev from JetBrains
 */
@SuppressWarnings("SpellCheckingInspection")
public abstract class Strings {



  @NotNull
  public static String replace(@NotNull final String where, @NotNull final String what, @NotNull final String with) {
    // TODO make my own implementation, because Java's one is so slow
    return where.replace(what, with);
  }


  @NotNull
  public static String repeat(@NotNull final String what, @Nullable final String delimiter, int count) {
    if (count == 0) return "";
    if (count == 1) return what;

    StringBuilder b = new StringBuilder((what.length()) + (delimiter == null ? 0 : delimiter.length()) * count);
    b.append(what);
    for (int i = 1; i < count; i++) {
      if (delimiter != null) b.append(delimiter);
      b.append(what);
    }

    return b.toString();
  }


  /**
   * Checks that the given string matches the specified pattern.
   *
   * <p>
   *   Nulls are acceptable, but for nulls it alwais returns false.
   * </p>
   *
   * @param string   the string to check (nulls are acceptable).
   * @param pattern  the pattern.
   * @return         whether matches.
   */
  @Contract("null,_->false")
  public static boolean matches(@Nullable final CharSequence string, @NotNull final Pattern pattern) {
    if (string == null) return false;
    return pattern.matcher(string).matches();
  }

  /**
   * Checks that the given string matches the specified pattern.
   *
   * <p>
   *   Nulls are acceptable, but for nulls it alwais returns false.
   * </p>
   *
   * @param string   the string to check (nulls are acceptable).
   * @param pattern  the pattern.
   * @param caseSensitive  check case sensitively.
   * @return         whether matches.
   */
  @Contract("null,_,_->false")
  public static boolean matches(@Nullable final CharSequence string, @NotNull final String pattern, boolean caseSensitive) {
    if (string == null) return false;
    Pattern p = Pattern.compile(pattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
    return matches(string, p);
  }


  /**
   * Converts the given string to the upper case.
   * Supports nulls.
   * @param string a string to convert, nulls are allowed.
   * @return  the given string in upper case, or null.
   * @see #lower
   */
  @Contract("!null->!null; null->null")
  public static String upper(@Nullable final String string) {
    return string == null ? null : string.toUpperCase();
  }


  /**
   * Converts the given string to the lower case.
   * Supports nulls.
   * @param string a string to convert, nulls are allowed.
   * @return  the given string in lower case, or null.
   * @see #upper
   */
  @Contract("!null->!null; null->null")
  public static String lower(@Nullable final String string) {
    return string == null ? null : string.toLowerCase();
  }


}
