package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;



/**
 * MS SQL Server facade.
 */
public final class MssqlFacade extends BaseFacade {

  /**
   * MS SQL Server JDBC driver. It can be in a separate class loader.
   */
  @NotNull
  private final Driver myDriver;



  public MssqlFacade(@NotNull final String connectionString,
                     @NotNull final Driver driver,
                     @NotNull final BaseErrorRecognizer errorRecognizer) {
    super(connectionString, errorRecognizer);
    myDriver = driver;
  }


  @NotNull
  @Override
  public final Rdbms getDbms() {
    return Rdbms.MSSQL;
  }



  @Override
  protected MssqlSession internalConnect() {
    Properties properties = new Properties();

    try {
      Connection connection = myDriver.connect(myConnectionString, properties);
      return new MssqlSession(this, connection, true);
    }
    catch (SQLException e) {
      throw myErrorRecognizer.recognizeError(e, "<connect>");
    }
  }


}
