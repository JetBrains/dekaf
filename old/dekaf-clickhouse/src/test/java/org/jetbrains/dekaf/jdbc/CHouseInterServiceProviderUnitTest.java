package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.CHouseIntermediateProvider.CHOUSE_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.CHouseIntermediateProvider.CHOUSE_CONNECTION_STRING_PATTERN;



public class CHouseInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(CHOUSE_CONNECTION_STRING_PATTERN)
                       .fits(CHOUSE_CONNECTION_STRING_EXAMPLE);
  }

}