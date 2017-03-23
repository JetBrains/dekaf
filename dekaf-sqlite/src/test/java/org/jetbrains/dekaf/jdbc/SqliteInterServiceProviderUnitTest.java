package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.SqliteIntermediateProvider.SQLITE_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.SqliteIntermediateProvider.SQLITE_CONNECTION_STRING_PATTERN;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SqliteInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(SQLITE_CONNECTION_STRING_PATTERN)
                       .fits(SQLITE_CONNECTION_STRING_EXAMPLE);
  }

}