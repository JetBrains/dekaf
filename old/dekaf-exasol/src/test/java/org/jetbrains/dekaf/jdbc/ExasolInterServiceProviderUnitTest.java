package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.ExasolIntermediateProvider.EXASOL_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.ExasolIntermediateProvider.EXASOL_CONNECTION_STRING_PATTERN;



public class ExasolInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(EXASOL_CONNECTION_STRING_PATTERN)
                       .fits(EXASOL_CONNECTION_STRING_EXAMPLE);
  }

}