package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.MssqlIntermediateProvider.MSSQL_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.MssqlIntermediateProvider.MSSQL_CONNECTION_STRING_PATTERN;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MssqlInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(MSSQL_CONNECTION_STRING_PATTERN)
                       .fits(MSSQL_CONNECTION_STRING_EXAMPLE);
  }

}