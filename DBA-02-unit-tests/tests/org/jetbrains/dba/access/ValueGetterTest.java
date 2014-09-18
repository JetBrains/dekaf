package org.jetbrains.dba.access;


import org.jetbrains.dba.junit.FineRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.jetbrains.dba.junit.Assertions.assertNotNull;
import static org.jetbrains.dba.junit.Assertions.assertSame;

/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
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
