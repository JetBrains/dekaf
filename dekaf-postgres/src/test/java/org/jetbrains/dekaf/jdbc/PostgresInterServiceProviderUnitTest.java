package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.PostgresIntermediateProvider.POSTGRES_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.PostgresIntermediateProvider.POSTGRES_CONNECTION_STRING_PATTERN;



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