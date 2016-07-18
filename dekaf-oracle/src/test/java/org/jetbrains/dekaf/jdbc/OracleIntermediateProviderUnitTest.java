package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.assertions.PatternAssert;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateRdbmsProvider;
import org.jetbrains.dekaf.util.Providers;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.OracleIntermediateProvider.ORACLE_CONNECTION_STRING_EXAMPLE;
import static org.jetbrains.dekaf.jdbc.OracleIntermediateProvider.ORACLE_CONNECTION_STRING_PATTERN;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
public class OracleIntermediateProviderUnitTest {


  @Test
  public void connectionStringExample_matches_connectionStringPattern() {
    PatternAssert.assertThat(ORACLE_CONNECTION_STRING_PATTERN)
                       .fits(ORACLE_CONNECTION_STRING_EXAMPLE);
  }

  @Test
  public void driver_is_loaded() throws SQLException {
    final Driver driver = DriverManager.getDriver(ORACLE_CONNECTION_STRING_EXAMPLE);
    assertThat(driver).isNotNull();
  }

  @Test
  public void provider_is_registered() {
    final Collection<IntegralIntermediateRdbmsProvider> providers =
            Providers.loadAllProviders(IntegralIntermediateRdbmsProvider.class, null);
    assertThat(providers).extracting("class").contains(OracleIntermediateProvider.class);
  }

}