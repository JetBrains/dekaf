package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.SybaseIntermediateProvider.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SybaseInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(SYBASE_CONNECTION_STRING_PATTERN)
                       .fits(SYBASE_JTDS_CONNECTION_STRING_EXAMPLE)
                       .fits(SYBASE_NATIVE_CONNECTION_STRING_EXAMPLE);
  }

}