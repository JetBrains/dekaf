package org.jetbrains.dekaf.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.util.Numbers.*;



/**
 * @author Leonid Bushuev
 */
@Tag("UnitTest")
@SuppressWarnings({"unused", "RedundantCast"})
public class NumbersTest {



    @Test
    void valuesAreEqual_basic() {
        assertThat(valuesAreEqual(123, 123L)).isTrue();
        assertThat(valuesAreEqual(123, BigInteger.valueOf(123L))).isTrue();
        assertThat(valuesAreEqual(123.45f, new BigDecimal("123.45"))).isTrue();
    }

    @Test
    void valuesAreEqual_nulls() {
        assertThat(valuesAreEqual(null, null)).isTrue();
        assertThat(valuesAreEqual(111, null)).isFalse();
        assertThat(valuesAreEqual(null, 222)).isFalse();
    }


    @ParameterizedTest
    @MethodSource("convert_basic$")
    void convert_basic(Number number, Class<Number> toClass) {
        Number convertedNumber = Numbers.convertNumber(toClass, number);
        assertThat(convertedNumber).isNotNull()
                                   .isExactlyInstanceOf(toClass);
        String originalString = Strings.removeEnding(number.toString(), ".0");
        String convertedString = Strings.removeEnding(convertedNumber.toString(), ".0");
        assertThat(convertedString).isEqualTo(originalString);
    }

    private static Object[][] convert_basic$() {
        return CONVERT_CASES;
    }

    private static final Object[][] CONVERT_CASES = new Object[][] {
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


    @ParameterizedTest
    @MethodSource("convertNumberSmartly_basic$")
    void convertNumberSmartly_basic(Number origin) {
      BigDecimal d = new BigDecimal(origin.toString());
      Number num = convertNumberSmartly(d);
      assertThat(num).isEqualTo(origin);
    }

    private static Number[] convertNumberSmartly_basic$() {
        return new Number[] {
            (byte) 0,
            (byte) -1,
            (byte) +1,
            (byte) -128,
            (byte) +127,
            (short) -129,
            (short) +128,
            (short) -32768,
            (short) +32767,
            (int) -32769,
            (int) +32768,
            (int) -32769,
            (int) -2147483648,
            (int) +2147483647,
            (long) -2147483649L,
            (long) +2147483648L,
            (long) -9223372036854775808L,
            (long) +9223372036854775807L,
            new BigInteger("-9223372036854775809"),
            new BigInteger("9223372036854775808"),
            (float) 123.456,
            (double) 123456.7890123,
            new BigDecimal("1234567890.123456789")
        };
    }


    @ParameterizedTest
    @MethodSource("parseIntSafe_with$")
    public void parseIntSafe_with(String str, int result) {
        final int value = parseIntSafe(str);
        assertThat(value).isEqualTo(result);
    }

    private static Object[][] parseIntSafe_with$() {
        return new Object[][] {
            {"123456", 123456},
            {"+2147483647", Integer.MAX_VALUE},
            {"-2147483648", Integer.MIN_VALUE},
            {"impossible", 0},
            {"", 0},
            {null, 0},
        };
    }


    @ParameterizedTest
    @MethodSource("parseLongSafe_with$")
    void parseLongSafe_with(String str, long result) {
        final long value = parseLongSafe(str);
        assertThat(value).isEqualTo(result);
    }

    private static Object[][] parseLongSafe_with$() {
        return new Object[][] {
            {"123456", 123456L},
            {"+9223372036854775807", Long.MAX_VALUE},
            {"-9223372036854775808", Long.MIN_VALUE},
            {"impossible", 0L},
            {"", 0L},
            {null, 0L},
        };
    }

}
