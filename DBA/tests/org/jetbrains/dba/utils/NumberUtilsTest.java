package org.jetbrains.dba.utils;

import org.jetbrains.dba.junit.FineRunner;
import org.jetbrains.dba.junit.TestWithParams;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.*;

import static org.jetbrains.dba.utils.NumberUtils.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(FineRunner.class)
public class NumberUtilsTest {


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
