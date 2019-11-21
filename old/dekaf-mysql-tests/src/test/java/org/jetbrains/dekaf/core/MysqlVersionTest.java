package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.util.Version;
import org.junit.Assert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.MysqlIntermediateFacade.parseServerVersion;



public class MysqlVersionTest {
  @Test
  public void test1() {
    doTest(
        "10.1.34-MariaDB-0ubuntu0.18.04.1",
        10, 1, 34);
  }

  @Test
  public void test2() {
    doTest(
        "10.1.31-MariaDB",
        10, 1, 31);
  }

  @Test
  public void test3() {
    doTest(
        "5.2.14-MariaDB-mariadb122~wheezy",
        5, 2, 14);
  }

  @Test
  public void test4() {
    doTest(
        "5.5.59-MariaDB-1~wheezy",
        5, 5, 59);
  }

  private void doTest(String verStr, int... ver) {
    Assert.assertEquals(Version.of(ver), parseServerVersion(verStr));
  }
}
