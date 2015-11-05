package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.jdba.jdbc.MssqlIntermediateProvider.MSSQL_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.jdba.jdbc.MssqlIntermediateProvider.MSSQL_CONNECTION_STRING_PATTERN;



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