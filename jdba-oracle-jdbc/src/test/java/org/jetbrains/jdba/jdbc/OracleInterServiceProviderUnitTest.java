package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.jdba.jdbc.OracleInterServiceProvider.ORACLE_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.jdba.jdbc.OracleInterServiceProvider.ORACLE_CONNECTION_STRING_PATTERN;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(ORACLE_CONNECTION_STRING_PATTERN)
                       .fits(ORACLE_CONNECTION_STRING_EXAMPLE);
  }

}