package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.H2dbIntermediateProvider.H2DB_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.H2dbIntermediateProvider.H2DB_CONNECTION_STRING_PATTERN;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class H2dbInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(H2DB_CONNECTION_STRING_PATTERN)
                       .fits(H2DB_CONNECTION_STRING_EXAMPLE);
  }

}