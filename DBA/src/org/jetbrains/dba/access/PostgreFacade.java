package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;

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
    super(connectionString, errorRecognizer);
    myDriver = driver;
  }


  @NotNull
  @Override
  public final Rdbms getDbms() {
    return Rdbms.POSTGRE;
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
      throw myErrorRecognizer.recognizeError(e);
    }
  }


}
