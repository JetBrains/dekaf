package org.jetbrains.dba.base;


import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ValueGetterTest {
  @Test
  public void test_forPrimitive_primitive() {
    final ValueGetter<Byte> getter = ValueGetters.of(byte.class);
    assertNotNull(getter);
    assertSame(getter, ValueGetters.ByteGetter.INSTANCE);
  }


  @Test
  public void test_forPrimitive_wrapper() {
    final ValueGetter<Byte> getter = ValueGetters.of(Byte.class);
    assertNotNull(getter);
    assertSame(getter, ValueGetters.ByteGetter.INSTANCE);
  }
}
