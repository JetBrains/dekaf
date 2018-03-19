package org.jetbrains.dekaf.util;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.util.Strings.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@SuppressWarnings("SpellCheckingInspection")
public class StringsTest {

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
