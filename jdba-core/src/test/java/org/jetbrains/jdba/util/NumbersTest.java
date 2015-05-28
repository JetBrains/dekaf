package org.jetbrains.jdba.util;

import org.jetbrains.jdba.junitft.FineRunner;
import org.jetbrains.jdba.junitft.TestWithParams;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.util.Numbers.parseIntSafe;
import static org.jetbrains.jdba.util.Numbers.parseLongSafe;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(FineRunner.class)
public class NumbersTest {


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
