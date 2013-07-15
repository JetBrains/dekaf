package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import static java.lang.String.format;



/**
 * Oracle DBMS facade.
 */
public final class PostgreFacade extends DatabaseAbstractFacade {

  /**
   * Oracle JDBC driver. It can be in a separate class loader.
   */
  @NotNull
  private final Driver myDriver;

  /**
   * Oracle specific errors recognizer.
   */
  @NotNull
  final DBErrorRecognizer myErrorRecognizer;


  public PostgreFacade(@NotNull final String connectionString,
                       @NotNull final Driver driver,
                       @NotNull final DBErrorRecognizer errorRecognizer) {
    super(connectionString);

    myDriver = driver;
    myErrorRecognizer = errorRecognizer;
  }


  @NotNull
  @Override
  public Rdbms getDbms() {
    return Rdbms.POSTGRE;
  }


  @NotNull
  @Override
  public DBErrorRecognizer getErrorRecognizer() {
    return myErrorRecognizer;
  }


  @Override
  protected PostgreSession internalConnect() {
    Properties properties = new Properties();

    try {
      final org.postgresql.Driver driver = (org.postgresql.Driver)myDriver;
      Connection connection = driver.connect(myConnectionString, properties);
      return new PostgreSession(this, connection, true);
    }
    catch (SQLException e) {
      throw myErrorRecognizer.recognizeError(e);
    }
  }


}
