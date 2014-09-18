package org.jetbrains.dba.junit;

/**
 * Assertions just for migration from TestNG.
 */
@Deprecated
public class LegacyAssertions extends org.assertj.core.api.Assertions {


  public static <T> void assertNotNull(T actual) {
    assertThat(actual).isNotNull();
  }

  public static <T> void assertNull(T actual) {
    assertThat(actual).isNull();
  }


  public static <T> void assertSame(T actual, T expected) {
    assertThat(actual).isSameAs(expected);
  }

  public static void assertTrue(boolean actual) {
    assertThat(actual).isTrue();
  }

  public static void assertFalse(boolean actual) {
    assertThat(actual).isFalse();
  }

}
