package org.jetbrains.jdba.core;

import org.assertj.core.api.Assertions;
import org.jetbrains.jdba.junitft.FineRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.sql.Types;



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
    ColumnBriefInfo[] columns = new ColumnBriefInfo[] {
        new ColumnBriefInfo("x", "x", Types.BOOLEAN),
        new ColumnBriefInfo("x", "x", Types.TINYINT),
        new ColumnBriefInfo("x", "x", Types.SMALLINT),
        new ColumnBriefInfo("x", "x", Types.INTEGER),
        new ColumnBriefInfo("x", "x", Types.BIGINT),
        new ColumnBriefInfo("x", "x", Types.FLOAT),
        new ColumnBriefInfo("x", "x", Types.DOUBLE)
    };

    StructRowFetcher<StructWithPrimitives> f =
      new StructRowFetcher<StructWithPrimitives>(columns, StructWithPrimitives.class);
    Assertions.assertThat(f.fields[0].getName()).isEqualTo("myBoolean");
    Assertions.assertThat(f.fields[0].getType()).isEqualTo((Class)boolean.class);
  }
}
