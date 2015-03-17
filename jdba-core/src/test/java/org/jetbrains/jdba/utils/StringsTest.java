package org.jetbrains.jdba.utils;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.utils.Strings.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
@SuppressWarnings("SpellCheckingInspection")
public class StringsTest {

  //// eq \\\\

  @Test
  public void eq_eq() {
    boolean ok = eq("A" + "BC", "AB" + "C");
    assertThat(ok).isTrue();
  }

  @Test
  public void eq_not_eq() {
    boolean ok = eq("XXX", "YYY");
    assertThat(ok).isFalse();
  }

  @Test
  public void eq_nulls() {
    boolean ok = eq(null, null);
    assertThat(ok).isTrue();
  }

  @Test
  public void eq_null_1() {
    boolean ok = eq(null, "ZZZ");
    assertThat(ok).isFalse();
  }

  @Test
  public void eq_null_2() {
    boolean ok = eq("ZZZ", null);
    assertThat(ok).isFalse();
  }


  //// ensureStartsWith and ensureEndsWith \\\\

  @Test
  public void ensureStartsWith_basic() {
    assertThat(ensureStartsWith("AAAA", 'C')).isEqualTo("CAAAA");
    assertThat(ensureStartsWith("CAAAA", 'C')).isEqualTo("CAAAA");
    assertThat(ensureStartsWith("", 'C')).isEqualTo("C");
  }

  @Test
  public void ensureStartsWith_dontMakeNewInstanceIfAlreadyStarts() {
    final String str = "CAAA";
    final String ensured = ensureStartsWith(str, 'C');
    assertThat(ensured).isSameAs(str);
  }

  @Test
  public void ensureEndsWith_basic() {
    assertThat(ensureEndsWith("AAAA", 'C')).isEqualTo("AAAAC");
    assertThat(ensureEndsWith("AAAAC", 'C')).isEqualTo("AAAAC");
    assertThat(ensureEndsWith("", 'C')).isEqualTo("C");
  }

  @Test
  public void ensureEndsWith_dontMakeNewInstanceIfAlreadyEnds() {
    final String str = "AAAC";
    final String ensured = ensureEndsWith(str, 'C');
    assertThat(ensured).isSameAs(str);
  }


  //// matches \\\\

  @Test
  public void matches_basic() {
    assertThat(matches("ABCDE", "ABCDE", false)).isTrue();
    assertThat(matches("ABCDE", "^ABCDE$", false)).isTrue();
    assertThat(matches("ABCDE", ".*BCD.*", false)).isTrue();
    assertThat(matches("ABCDE", "^.*BCD.*$", false)).isTrue();

    assertThat(matches("ABXDE", "^.*BCD.*$", false)).isFalse();
  }

  @Test
  public void matches_caseSensitivity() {
    assertThat(matches("ABCDE", "abcde", false)).isTrue();
    assertThat(matches("ABCDE", "abcde", true)).isFalse();
  }

  @Test
  public void matches_nulls() {
    assertThat(matches(null, "abcde", false)).isFalse();
    assertThat(matches(null, "abcde", true)).isFalse();

    assertThat(matches(null, Pattern.compile("abcde"))).isFalse();
  }


  //// upper and lower \\\\

  @Test
  public void upper_basic() {
    assertThat(upper("aaA")).isEqualTo("AAA");
    assertThat(upper("i")).isEqualTo("I");
    assertThat(upper("M")).isEqualTo("M");

    assertThat(upper(null)).isNull();

    assertThat(upper("XXX")).isSameAs("XXX");
  }

  @Test
  public void lower_basic() {
    assertThat(lower("Aaa")).isEqualTo("aaa");
    assertThat(lower("Iii")).isEqualTo("iii");
    assertThat(lower("xxx")).isEqualTo("xxx");

    assertThat(lower(null)).isNull();

    assertThat(lower("xxx")).isSameAs("xxx");
  }

}
