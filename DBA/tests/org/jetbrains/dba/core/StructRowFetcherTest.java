package org.jetbrains.dba.core;

import org.assertj.core.api.Assertions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import testing.junit.FineRunner;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
public class StructRowFetcherTest {

  final static class StructWithPrimitives {
    boolean myBoolean;
    byte myByte;
    short myShort;
    int myInt;
    long myLong;
    float myFloat;
    double myDouble;
  }


  @Test
  public void test_for_StructRowFetcherTest() {
    StructRowFetcher<StructWithPrimitives> f =
      new StructRowFetcher<StructWithPrimitives>(StructWithPrimitives.class);
    Assertions.assertThat(f.fields[0].getName()).isEqualTo("myBoolean");
    Assertions.assertThat(f.fields[0].getType()).isEqualTo((Class)boolean.class);
  }
}
