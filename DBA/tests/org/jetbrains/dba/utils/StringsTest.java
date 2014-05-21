package org.jetbrains.dba.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import static org.jetbrains.dba.utils.Strings.eq;



/**
 * @author Leonid Bushuev from JetBrains
 */
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

}
