package org.jetbrains.dba;

import oracle.jdbc.OracleDriver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.errors.DBDriverError;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import static java.lang.String.format;



/**
 * Oracle DBMS facade.
 */
public final class OraFacade extends DatabaseAbstractFacade {

  /**
   * Oracle JDBC driver. It can be in a separate class loader.
   */
  @NotNull
  private final OracleDriver myDriver;

  /**
   * Oracle specific errors recognizer.
   */
  @NotNull
  final DBErrorRecognizer myErrorRecognizer;


  public OraFacade(@NotNull final String connectionString,
                   @NotNull final Driver driver,
                   @NotNull final DBErrorRecognizer errorRecognizer) {
    super(connectionString);
    if (!(driver instanceof OracleDriver)) {
      throw new DBDriverError(format("Got class %s when expected OracleDriver", driver.getClass().getName()));
    }
    myDriver = (OracleDriver)driver;
    myErrorRecognizer = errorRecognizer;
  }


  @NotNull
  @Override
  public Rdbms getDbms() {
    return Rdbms.ORACLE;
  }


  @NotNull
  @Override
  public DBErrorRecognizer getErrorRecognizer() {
    return myErrorRecognizer;
  }


  @Override
  protected OraSession internalConnect() {
    Properties properties = new Properties();

    try {
      Connection connection = myDriver.connect(myConnectionString, properties);
      return new OraSession(this, connection, true);
    }
    catch (SQLException e) {
      throw myErrorRecognizer.recognizeError(e);
    }
  }


}
