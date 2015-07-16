package org.jetbrains.jdba.util;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


/**
 * Version of a product.
 * Holds one or more numbers of the version.
 * Value object (immutable). Comparable. Useful. Quadratisch. Praktisch. Gut.
 *
 * <p>
 *   How to use:
 *   <pre>
 *     Version v = Version.of(System.getProperty("java.runtime.version"));
 *     if (v.compareTo(Version.of(1,7)) < 0) { ... }
 *   </pre>
 * </p>
 *
 * <p>
 *   Note: the tailing zeros are not meaningful,
 *   in other words, versions 1.2.3 and 1.2.3.0.0 are equal.
 * </p>
 *
 * @author Leonid Bushuev from JetBrains
 * @since 8.1.3.
 */
public final class Version implements Comparable<Version>, Serializable {

  @NotNull
  private final List<Integer> elements;


  /**
   * Parse the version string.
   * @param string version string like "1.2.3-4"
   * @return   the parsed version.
   */
  public static Version of(@NotNull final String string) {
    ImmutableList.Builder<Integer> b = ImmutableList.builder();
    final String[] substrings = string.split("[\\.\\,\\-_ ]");
    for (String ss : substrings) {
      String ss2 = ss.trim();
      if (ss2.isEmpty()) continue;
      try {
        Integer v = new Integer(ss2);
        b.add(v);
      }
      catch (NumberFormatException e) {
        break;
      }
    }
    ImmutableList<Integer> elements = b.build();
    if (elements.isEmpty()) throw new IllegalArgumentException("Failed to parse version \""+string+"\"");
    if (elements.size() == 1 && elements.get(0) == 0) return ZERO;

    return new Version(elements);
  }

  /**
   * Trivial constructor - makes the version by it elements.
   * @param elements elements, must contain at least one element.
   * @return         the version.
   */
  public static Version of(@NotNull final Integer... elements) {
    if (elements.length == 1 && elements[0] == 0) return ZERO;
    return new Version(ImmutableList.copyOf(elements));
  }


  private Version(@NotNull final List<Integer> elements) {
    int n = elements.size();
    while (n > 0 && elements.get(n-1) == 0) n--;
    this.elements = ImmutableList.copyOf(elements.subList(0,n));
  }


  /**
   * The i-th element, if specified, or 0 if not.
   *
   * <p>
   *   So, for version 1.2.3 first three elements will be 1,2,3,
   *   and the rest elements will be 0.
   * </p>
   *
   * <p>
   *   In other words, versions 1.2.3.0.0 and 1.2.3 are equal.
   * </p>
   *
   * @param index element index (started with zero).
   * @return the i-th element.
   */
  public int get(int index) {
    return index < elements.size() ? elements.get(index) : 0;
  }


  public int size() {
    return elements.size();
  }


  /**
   * Compares to the specified version.
   * @param that  version to compare with.
   * @return      <b>+1</b> when this version is greater than the specified,
   *              <b>-1</b> when this version is less than the specified,
   *              <b>0</b> when they equal.
   */
  public int compareTo(@NotNull final Version that) {
    if (this == that) return 0;

    int m = Math.max(this.size(), that.size());
    for (int i = 0; i < m; i++) {
      int z = this.get(i) - that.get(i);
      if (z > 0) return +1;
      if (z < 0) return -1;
    }

    return 0;
  }


  /**
   * Compares to the specified version.
   * @param that  version to compare with.
   * @return      <b>+1</b> when this version is greater than the specified,
   *              <b>-1</b> when this version is less than the specified,
   *              <b>0</b> when they equal.
   */
  public int compareTo(final int... that) {
    int m = Math.max(this.size(), that.length);
    for (int i = 0; i < m; i++) {
      int z = this.get(i) - (i < that.length ? that[i] : 0);
      if (z > 0) return +1;
      if (z < 0) return -1;
    }

    return 0;
  }

  /**
   * Checks whether the version is equal to or gerater than the specified version.
   * @param than v ersion to compare.
   * @return <i>true</i> if equals to or is greater than.
   */
  public boolean isOrGreater(final int... than) {
    return compareTo(than) >= 0;
  }


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Version that = (Version)o;

    return this.elements.equals(that.elements);
  }

  @Override
  public int hashCode() {
    return elements.hashCode();
  }

  /**
   * Comma delimited version string.
   * @return dot delimited version string.
   * @see #toString(int, int)
   */
  @NotNull
  public String toString() {
    return toString(2,100);
  }

  /**
   * Comma delimited version string.
   * @param minimumElements  minimum elements to use.
   * @param maximumElements  maximum elements to use.
   * @return dot delimited version string.
   */
  @NotNull
  public String toString(int minimumElements, int maximumElements) {
    int n = Math.min(Math.max(elements.size(), minimumElements), maximumElements);
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < n; i++) {
      if (i > 0) b.append('.');
      b.append(get(i));
    }
    return b.toString();
  }


  public static final Version ZERO = new Version(Collections.singletonList(0));
}
