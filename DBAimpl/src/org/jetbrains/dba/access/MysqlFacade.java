package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;



/**
 * MySQL facade.
 */
public final class MysqlFacade extends BaseFacade {

  /**
   * MySQL JDBC driver (aka connector). It can be in a separate class loader.
   */
  @NotNull
  private final Driver myDriver;



  public MysqlFacade(@NotNull final String connectionString,
                     @NotNull final Driver driver,
                     @NotNull final BaseErrorRecognizer errorRecognizer) {
    super(connectionString, errorRecognizer);
    myDriver = driver;
  }


  @NotNull
  @Override
  public final Rdbms getDbms() {
    return Rdbms.MYSQL;
  }



  @Override
  protected MysqlSession internalConnect() {
    Properties properties = new Properties();

    try {
      Connection connection = myDriver.connect(myConnectionString, properties);
      return new MysqlSession(this, connection, true);
    }
    catch (SQLException e) {
      throw myErrorRecognizer.recognizeError(e);
    }
  }


}
