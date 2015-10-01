package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.jdba.jdbc.PostgresIntermediateProvider.POSTGRES_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.jdba.jdbc.PostgresIntermediateProvider.POSTGRES_CONNECTION_STRING_PATTERN;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgresInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(POSTGRES_CONNECTION_STRING_PATTERN)
                       .fits(POSTGRES_CONNECTION_STRING_EXAMPLE);
  }

}