package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.jdba.jdbc.MysqlIntermediateProvider.MYSQL_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.jdba.jdbc.MysqlIntermediateProvider.MYSQL_CONNECTION_STRING_PATTERN;



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