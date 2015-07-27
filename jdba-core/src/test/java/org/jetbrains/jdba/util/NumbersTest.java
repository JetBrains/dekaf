package org.jetbrains.jdba.util;

import org.jetbrains.jdba.junitft.FineRunner;
import org.jetbrains.jdba.junitft.TestWithParams;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.util.Numbers.*;
import static org.jetbrains.jdba.util.Strings.removeEnding;



/**
 * @author Leonid Bushuev from JetBrains
 */
@SuppressWarnings("unused")
@RunWith(FineRunner.class)
public class NumbersTest {


  @Test
  public void valuesAreEqual_basic() {
    assertThat(valuesAreEqual(123, 123L)).isTrue();
    assertThat(valuesAreEqual(123, BigInteger.valueOf(123L))).isTrue();
    assertThat(valuesAreEqual(123.45f, new BigDecimal("123.45"))).isTrue();
  }

  @Test
  public void valuesAreEqual_nulls() {
    assertThat(valuesAreEqual(null, null)).isTrue();
    assertThat(valuesAreEqual(111, null)).isFalse();
    assertThat(valuesAreEqual(null, 222)).isFalse();
  }


  static final Object[][] CONVERT_CASES = new Object[][] {
      { (byte) 127,                      Byte       .class },
      { (byte) 127,                      Short      .class },
      { (byte) 127,                      Integer    .class },
      { (byte) 127,                      Long       .class },
      { (byte) 127,                      Float      .class },
      { (byte) 127,                      Double     .class },
      { (byte) 127,                      BigInteger .class },
      { (byte) 127,                      BigDecimal .class },
      //
      { (short) 100,                     Byte       .class },
      { (short) 32767,                   Short      .class },
      { (short) 32767,                   Integer    .class },
      { (short) 32767,                   Long       .class },
      { (short) 32767,                   Float      .class },
      { (short) 32767,                   Double     .class },
      { (short) 32767,                   BigInteger .class },
      { (short) 32767,                   BigDecimal .class },
      //
      { 100,                             Byte       .class },
      { 32767,                           Short      .class },
      { 2000000000,                      Integer    .class },
      { 2000000000,                      Long       .class },
      { 1000001,                         Float      .class },
      { 2000000001,                      BigInteger .class },
      { 2000000001,                      BigDecimal .class },
      //
      { 100L,                            Byte       .class },
      { 32767L,                          Short      .class },
      { 2147483647L,                     Integer    .class },
      { 9223372036854775807L,            Long       .class },
      { 9223372036854775807L,            BigInteger .class },
      { 9223372036854775807L,            BigDecimal .class },
      //
      { BigInteger.valueOf(100),         Byte       .class },
      { BigInteger.valueOf(1000),        Short      .class },
      { BigInteger.valueOf(1000000),     Integer    .class },
      { BigInteger.valueOf(7000000000L), Long       .class },
      { BigInteger.valueOf(7000000000L), BigInteger .class },
      { BigInteger.valueOf(7000000000L), BigDecimal .class },
      //
      { BigDecimal.valueOf(100),         Byte       .class },
      { BigDecimal.valueOf(1000),        Short      .class },
      { BigDecimal.valueOf(1000000),     Integer    .class },
      { BigDecimal.valueOf(7000000000L), Long       .class },
      { BigDecimal.valueOf(7000000000L), BigInteger .class },
      { BigDecimal.valueOf(7000000000L), BigDecimal .class },
      //
      { 0.5f,                            Double     .class },
      { 1.0625f,                         BigDecimal .class },
      { 123456.7890123456,               BigDecimal .class },
  };


  @TestWithParams(params = "CONVERT_CASES")
  public void convert_basic(Number number, Class<Number> toClass) {
    Number convertedNumber = Numbers.convertNumber(toClass, number);
    assertThat(convertedNumber).isNotNull()
                               .isExactlyInstanceOf(toClass);
    String originalString = removeEnding(number.toString(), ".0");
    String convertedString = removeEnding(convertedNumber.toString(),".0");
    assertThat(convertedString).isEqualTo(originalString);
  }


  static Object[][] INT_CASES = new Object[][] {
    { "123456", 123456 },
    { "+2147483647", Integer.MAX_VALUE },
    { "-2147483648", Integer.MIN_VALUE },
    { "impossible", 0 },
    { "", 0 },
    { null, 0 },
  };


  @TestWithParams(params = "INT_CASES")
  public void parseIntSafe_with(String str, int result) {
    final int value = parseIntSafe(str);
    assertThat(value).isEqualTo(result);
  }


  static Object[][] LONG_CASES = new Object[][] {
    { "123456", 123456 },
    { "+9223372036854775807", Long.MAX_VALUE },
    { "-9223372036854775808", Long.MIN_VALUE },
    { "impossible", 0 },
    { "", 0 },
    { null, 0 },
  };


  @TestWithParams(params = "LONG_CASES")
  public void parseLongSafe_with(String str, long result) {
    final long value = parseLongSafe(str);
    assertThat(value).isEqualTo(result);
  }

}
