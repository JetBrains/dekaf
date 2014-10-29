package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.sql.OraSQL;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import static org.jetbrains.dba.KnownRdbms.ORACLE;



/**
 * Oracle DBMS facade.
 */
public final class OraFacade extends BaseFacade {

  /**
   * Oracle JDBC driver. It can be in a separate class loader.
   */
  @NotNull
  private final Driver myDriver;


  public OraFacade(@NotNull final String connectionString,
                   @NotNull final Driver driver,
                   @NotNull final BaseErrorRecognizer errorRecognizer) {
    super(connectionString, new OraSQL(), errorRecognizer);
    /*
    if (!(driver instanceof OracleDriver)) {
      throw new DBDriverError(format("Got class %s when expected OracleDriver", driver.getClass().getName()));
    }
    myDriver = (OracleDriver)driver;
    */
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
    return ORACLE;
  }


  @Override
  protected OraSession internalConnect() {
    Properties properties = new Properties();

    try {
      Connection connection = myDriver.connect(myConnectionString, properties);
      return new OraSession(this, connection, true);
    }
    catch (SQLException e) {
      throw myErrorRecognizer.recognizeError(e, "<connect>");
    }
  }


}
