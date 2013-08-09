package org.jetbrains.dba.access;

import org.jetbrains.dba.access.StructRowFetcher;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;



/**
 * @author Leonid Bushuev from JetBrains
 */
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
    assertEquals(f.fields[0].getName(), "myBoolean");
    assertEquals(f.fields[0].getType(), (Class)boolean.class);
  }
}
