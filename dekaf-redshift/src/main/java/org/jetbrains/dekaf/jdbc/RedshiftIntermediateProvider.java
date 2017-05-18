package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.Redshift;
import org.jetbrains.dekaf.exceptions.DBInitializationException;
import org.jetbrains.dekaf.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;


public class RedshiftIntermediateProvider extends JdbcIntermediateRdbmsProvider {

  public static final RedshiftIntermediateProvider INSTANCE = new RedshiftIntermediateProvider();

  static final Pattern REDSHIFT_CONNECTION_STRING_PATTERN =
      Pattern.compile("^jdbc:redshift://.+$");

  static final String REDSHIFT_CONNECTION_STRING_EXAMPLE = "jdbc:redshift:///";

  static final String REDSHIFT_DRIVER_CLASS_NAME = "com.amazon.redshift.jdbc.Driver";

  @Nullable
  @Override
  protected String getConnectionStringExample() {
    return REDSHIFT_CONNECTION_STRING_EXAMPLE;
  }

  @Nullable
  @Override
  protected Driver loadDriver() {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(REDSHIFT_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBInitializationException("Redshift Driver class not found");
    }

    final Driver driver;
    try {
      driver = driverClass.newInstance();
    }
    catch (Exception e) {
      throw new DBPreparingException("Failed to instantiate driver: "+e.getMessage(), e);
    }

    return driver;
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return RedshiftExceptionRecognizer.INSTANCE;
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Redshift.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return REDSHIFT_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }

  @NotNull
  @Override
  protected JdbcIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                     @Nullable final Properties connectionProperties,
                                                     final int connectionsLimit,
                                                     @NotNull final Driver driver) {
    return new RedshiftIntermediateFacade(
        connectionString,
        connectionProperties,
        driver,
        connectionsLimit,
        RedshiftExceptionRecognizer.INSTANCE
    );
  }
}
