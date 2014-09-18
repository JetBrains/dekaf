package org.jetbrains.dba.junit;

/**
 * Useful assertion methods.
 * @author Leonid Bushuev from JetBrains
 */
public class Assertions extends org.assertj.core.api.Assertions {


  public static <T> void assertNotNull(T actual) {
    assertThat(actual).isNotNull();
  }

  public static <T> void assertNull(T actual) {
    assertThat(actual).isNull();
  }


  public static <T> void assertEquals(T actual, T expected) {
    assertThat(actual).isEqualTo(expected);
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
