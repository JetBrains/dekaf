package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.MysqlIntermediateProvider.MYSQL_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.MysqlIntermediateProvider.MYSQL_CONNECTION_STRING_PATTERN;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MysqlInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(MYSQL_CONNECTION_STRING_PATTERN)
                       .fits(MYSQL_CONNECTION_STRING_EXAMPLE);
  }

}