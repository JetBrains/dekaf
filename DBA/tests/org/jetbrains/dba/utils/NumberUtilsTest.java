package org.jetbrains.dba.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import static org.jetbrains.dba.utils.NumberUtils.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class NumberUtilsTest {

  //// parseIntSafe \\\\

  @Test
  public void parseIntSafe_basic() {
    final int value = parseIntSafe("123456");
    assertThat(value).isEqualTo(123456);
  }

  @Test
  public void parseIntSafe_positive() {
    final int value = parseIntSafe("+2147483647");
    assertThat(value).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  public void parseIntSafe_negative() {
    final int value = parseIntSafe("-2147483648");
    assertThat(value).isEqualTo(Integer.MIN_VALUE);
  }

  @Test
  public void parseIntSafe_exception() {
    final int value = parseIntSafe("impossible");
    assertThat(value).isZero();
  }

  @Test
  public void parseIntSafe_null() {
    final int value = parseIntSafe(null);
    assertThat(value).isZero();
  }

  @Test
  public void parseIntSafe_emptyString() {
    final int value = parseIntSafe(null);
    assertThat(value).isZero();
  }

  
  //// parseLongSafe \\\\

  @Test
  public void parseLongSafe_basic() {
    final Long value = parseLongSafe("123456");
    assertThat(value).isEqualTo(123456);
  }

  @Test
  public void parseLongSafe_positive() {
    final Long value = parseLongSafe("+9223372036854775807");
    assertThat(value).isEqualTo(Long.MAX_VALUE);
  }

  @Test
  public void parseLongSafe_negative() {
    final Long value = parseLongSafe("-9223372036854775808");
    assertThat(value).isEqualTo(Long.MIN_VALUE);
  }

  @Test
  public void parseLongSafe_exception() {
    final Long value = parseLongSafe("impossible");
    assertThat(value).isZero();
  }

  @Test
  public void parseLongSafe_null() {
    final Long value = parseLongSafe(null);
    assertThat(value).isZero();
  }

  @Test
  public void parseLongSafe_emptyString() {
    final Long value = parseLongSafe(null);
    assertThat(value).isZero();
  }
}
