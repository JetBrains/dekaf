package org.jetbrains.dekaf.jdbc;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.RedshiftIntermediateProvider.REDSHIFT_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.RedshiftIntermediateProvider.REDSHIFT_CONNECTION_STRING_PATTERN;



public class RedshiftInterServiceProviderUnitTest {

  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    assertThat(REDSHIFT_CONNECTION_STRING_EXAMPLE).matches(REDSHIFT_CONNECTION_STRING_PATTERN);
  }

}
