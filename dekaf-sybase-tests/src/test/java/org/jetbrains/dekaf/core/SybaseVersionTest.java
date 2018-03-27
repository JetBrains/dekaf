package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.util.Version;
import org.junit.Assert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.SybaseIntermediateFacade.parseServerVersion;



public class SybaseVersionTest {
  @Test
  public void test1() {
    doTest(
        "Adaptive Server Enterprise/15.7.0/EBF 19805 SMP ESD#01 /P/x86_64/Enterprise Linux/aseasap/2918/64-bit/FBO/Wed Feb  8 07:50:28 2012",
        15, 7);
  }

  @Test
  public void test2() {
    doTest(
        "Adaptive Server Enterprise/16.0 SP02 PL02/EBF 25320 SMP/P/x86_64/Enterprise Linux/ase160sp02plx/2492/64-bit/FBO/Sat Nov 21 04:05:39 2015",
        16, 0);
  }

  private void doTest(String verStr, int... ver) {
    Assert.assertEquals(Version.of(ver), parseServerVersion(verStr));
  }
}
