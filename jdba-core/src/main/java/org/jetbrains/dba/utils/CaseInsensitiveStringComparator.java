package org.jetbrains.dba.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;



/**
 *
 **/
public final class CaseInsensitiveStringComparator implements Comparator<String> {
  @Override
  public int compare(final String str1, final String str2) {
    if (str1 == str2) {
      return 0;
    }
    if (str1 == null || str2 == null) {
      throw new NullPointerException("One of given strings is null");
    }
    return str1.compareToIgnoreCase(str2);
  }


  @NotNull
  public static final CaseInsensitiveStringComparator instance =
    new CaseInsensitiveStringComparator();
}
