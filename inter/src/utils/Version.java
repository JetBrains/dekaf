package org.jetbrains.dekaf.inter.utils;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;


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
 * @author Leonid Bushuev
 */
@SuppressWarnings("serial")
public final class Version implements Comparable<Version>, Serializable {

  private static final Map<String, Integer> SPECIAL_VALUES = new HashMap<String, Integer>();

  static {
    SPECIAL_VALUES.put("alpha", -30);
    SPECIAL_VALUES.put("beta", -20);
    SPECIAL_VALUES.put("rc", -10);
  }


  @NotNull
  private final int[] elements;


  /**
   * Parse the version string.
   * @param string version string like "1.2.3-4"
   * @return   the parsed version.
   * @see #of(int...)
   */
  public static Version of(@NotNull final String string) {
    ArrayList<Integer> b = new ArrayList<Integer>(5);
    final String[] substrings = string.split("[.,\\-_ ]|(?<=\\d)(?!\\d)|(?<!\\d)(?=\\d)");
    for (String ss : substrings) {
      String ss2 = ss.trim().toLowerCase(Locale.ENGLISH);
      if (ss2.isEmpty()) continue;
      Integer special = SPECIAL_VALUES.get(ss2);
      if (special != null) b.add(special);
      else {
        try {
          Integer v = Integer.valueOf(ss2);
          b.add(v);
        } catch (NumberFormatException e) {
          break;
        }
      }
    }
    if (b.isEmpty()) throw new IllegalArgumentException("Failed to parse version \""+string+"\"");
    if (b.size() == 1 && b.get(0) == 0) return ZERO;

    return of(b.toArray(new Integer[b.size()]));
  }

  /**
   * Trivial constructor - makes the version by it elements.
   * @param elements elements, must contain at least one element.
   * @return         the version.
   */
  public static Version of(@NotNull final int... elements) {
    int n = elements.length;
    while (n > 0 && elements[n-1] == 0) n--;
    if (n == 0) return ZERO;
    int[] ownElements = new int[n];
    System.arraycopy(elements, 0, ownElements, 0, n);
    return new Version(ownElements);
  }

  /**
   * Trivial constructor - makes the version by it elements.
   * @param elements elements, must contain at least one element.
   * @return         the version.
   * @see #of(int...)
   */
  public static Version of(@NotNull final Integer[] elements) {
    int n = elements.length;
    while (n > 0 && elements[n-1] == 0) n--;
    if (n == 0) return ZERO;
    int[] ownElements = new int[n];
    for (int i = 0; i < n; i++) ownElements[i] = elements[i];
    return new Version(ownElements);
  }


  private Version(@NotNull final int[] elements) {
    this.elements = elements; // already copied
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
    return index < elements.length ? elements[index] : 0;
  }


  /**
   * Number of elements (when the last element is not zero).
   * @return number of elements, or 0 if the version is ZERO.
   */
  public int size() {
    return elements.length;
  }


  /**
   * Truncate to get the version with no more <b><i>n</i></b> elements.
   * @param n how many elements to preserve.
   * @return  the result with the size no more than <b><i>n</i></b>.
   */
  @NotNull
  public Version truncate(final int n) {
    if (elements.length <= n) return this;
    if (n < 0) throw new IllegalArgumentException("Negative desired size: " + n);
    int m = n;
    while (m > 0 && elements[m-1] == 0) m--;
    if (m == 0) return ZERO;
    final int[] newElements = new int[m];
    System.arraycopy(this.elements, 0, newElements, 0, m);
    return new Version(newElements);
  }

  /**
   * Truncate since the first negative element. This is needed for removing 'beta' marks.
   * @return the version without negative elements.
   */
  @NotNull
  public Version truncateNegatives() {
    final int n = elements.length;
    for (int k = 0; k < n; k++) if (elements[k] < 0) return truncate(k);
    return this;
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
      int z = compare(this.get(i), that.get(i));
      if (z != 0) return z;
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
      int z = compare(this.get(i), (i < that.length ? that[i] : 0));
      if (z != 0) return z;
    }

    return 0;
  }

  private static int compare(final int x, final int y) {
    return x < y ? -1 : x > y ? +1 : 0;
  }

  /**
   * Checks whether the version is equal to or grater than the specified version.
   * @param than the version to compare.
   * @return <i>true</i> if equals to or is greater than the other.
   */
  public boolean isOrGreater(final int... than) {
    return compareTo(than) >= 0;
  }

  /**
   * Checks whether the version is equal to or grater than the specified version.
   * @param than the version to compare.
   * @return <i>true</i> if equals to or is greater than the other.
   */
  public boolean isOrGreater(@NotNull final Version than) {
    return compareTo(than) >= 0;
  }

  /**
   * Checks whether the version is less than the specified version.
   * @param than the version to compare.
   * @return <i>true</i> if less than the other.
   */
  public boolean less(final int... than) {
    return compareTo(than) < 0;
  }

  /**
   * Checks whether the version is less than the specified version.
   * @param than the version to compare.
   * @return <i>true</i> if less than the other.
   */
  public boolean less(@NotNull final Version than) {
    return compareTo(than) < 0;
  }


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Version that = (Version)o;

    return Arrays.equals(this.elements, that.elements);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(elements);
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
    int n = Math.min(Math.max(elements.length, minimumElements), maximumElements);
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < n; i++) {
      if (i > 0) b.append('.');
      b.append(get(i));
    }
    return b.toString();
  }


  /**
   * Returns this version as an array of elements.
   * @return array of elements, may be an empty array but never null.
   */
  @NotNull
  public int[] toArray() {
    final int n = elements.length;
    final int[] result = new int[n];
    System.arraycopy(elements, 0, result, 0, n);
    return result;
  }


  /**
   * ZERO version — 0.0
   */
  public static final Version ZERO = new Version(new int[0]);
  
}
