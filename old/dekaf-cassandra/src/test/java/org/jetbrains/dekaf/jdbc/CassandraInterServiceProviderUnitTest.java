package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.junit.Test;

import static org.jetbrains.dekaf.jdbc.CassandraIntermediateProvider.CASSANDRA_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.CassandraIntermediateProvider.CASSANDRA_CONNECTION_STRING_PATTERN;



public class CassandraInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(CASSANDRA_CONNECTION_STRING_PATTERN)
                 .fits(CASSANDRA_CONNECTION_STRING_EXAMPLE);
  }

}
