package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.KnownRdbms;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.sql.SQL;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;




/**
 * PostgreSQL facade.
 */
public final class PostgreFacade extends BaseFacade {

  /**
   * Oracle JDBC driver. It can be in a separate class loader.
   */
  @NotNull
  private final /*org.postgresql.*/Driver myDriver;



  public PostgreFacade(@NotNull final String connectionString,
                       @NotNull final Driver driver,
                       @NotNull final BaseErrorRecognizer errorRecognizer) {
    super(connectionString, new SQL(), errorRecognizer);
    myDriver = driver;
  }


  @NotNull
  @Override
  protected Driver getDriver() {
    return myDriver;
  }


  @NotNull
  @Override
  public final Rdbms rdbms() {
    return KnownRdbms.POSTGRE;
  }



  @Override
  protected PostgreSession internalConnect() {
    Properties properties = new Properties();

    try {
      Connection connection = myDriver.connect(myConnectionString, properties);
      /*
      final org.postgresql.Driver driver = (org.postgresql.Driver)myDriver;
      Connection connection = driver.connect(myConnectionString, properties);
      */
      return new PostgreSession(this, connection, true);
    }
    catch (SQLException e) {
      throw myErrorRecognizer.recognizeError(e, "<connect>");
    }
  }


}
