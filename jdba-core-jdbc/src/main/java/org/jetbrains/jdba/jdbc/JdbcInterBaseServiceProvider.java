package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.DBInterFacade;
import org.jetbrains.jdba.core.DBInterRdbmsServiceProvider;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcInterBaseServiceProvider implements DBInterRdbmsServiceProvider {


  @Override
  public DBInterFacade openFacade(@NotNull final String connectionString,
                                  @Nullable final Properties connectionProperties,
                                  final int connectionsLimit) {
    Driver driver = getDriver(connectionString);
    BaseErrorRecognizer errorRecognizer = getErrorRecognizer();
    return new JdbcInterFacade(connectionString, connectionProperties, driver, connectionsLimit, errorRecognizer);
  }


  protected Driver getDriver(@NotNull final String connectionString) {
    try {
      return DriverManager.getDriver(connectionString);
    }
    catch (SQLException sqle) {
      throw getErrorRecognizer().recognizeError(sqle, "DriverManager.getDriver for: " + connectionString);
    }
  }


  public abstract BaseErrorRecognizer getErrorRecognizer();

}
