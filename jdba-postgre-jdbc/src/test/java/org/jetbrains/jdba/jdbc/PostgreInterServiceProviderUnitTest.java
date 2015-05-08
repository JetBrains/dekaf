package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.jdba.jdbc.PostgreProvider.POSTGRE_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.jdba.jdbc.PostgreProvider.POSTGRE_CONNECTION_STRING_PATTERN;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(POSTGRE_CONNECTION_STRING_PATTERN)
                       .fits(POSTGRE_CONNECTION_STRING_EXAMPLE);
  }

}