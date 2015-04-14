package org.jetbrains.jdba.core;


import org.junit.Test;

import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Leonid Bushuev from JetBrains
 */
public class ValueGetterTest {

  @Test
  public void primitive_byte() {
    final ValueGetter<Byte> getter = ValueGetters.find(Types.TINYINT, byte.class);
    assertThat(getter).isNotNull()
                      .isSameAs(ValueGetters.ByteGetter.INSTANCE);
  }


  @Test
  public void primitive_bool_bool() {
    final ValueGetter<Boolean> getter = ValueGetters.find(Types.BOOLEAN, Boolean.class);
    assertThat(getter).isNotNull()
                      .isSameAs(ValueGetters.BoolBoolGetter.INSTANCE);
  }

  @Test
  public void primitive_bit_bool() {
    final ValueGetter<Boolean> getter = ValueGetters.find(Types.BIT, Boolean.class);
    assertThat(getter).isNotNull()
                      .isSameAs(ValueGetters.BoolBoolGetter.INSTANCE);
  }

  @Test
  public void primitive_int_bool() {
    final ValueGetter<Boolean> getter = ValueGetters.find(Types.INTEGER, Boolean.class);
    assertThat(getter).isNotNull()
                      .isSameAs(ValueGetters.IntBoolGetter.INSTANCE);
  }


}
