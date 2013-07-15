package org.jetbrains.dba.base;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.errors.DBDriverError;
import org.jetbrains.dba.errors.DbmsUnsupportedFeatureError;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.String.format;
import static org.jetbrains.dba.utils.Strings.matches;



/**
 * Implements {@link DBProvider}.
 *
 * <p>
 *   This provider is responsible for maintain JDBC drivers with their class loader,
 *   determining database connection strings, etc.
 * </p>
 *
 * <p>
 *   A good candidate to place into an IoC container.
 * </p>
 *
 * @author Leonid Bushuev from JetBrains
 */
public final class JdbcDBProvider implements DBProvider {

  private final List<JdbcDriverDef> myDriverDefs = new CopyOnWriteArrayList<JdbcDriverDef>(
    Arrays.asList(
      new JdbcDriverDef(Rdbms.POSTGRE, "^jdbc:postgresql:.*$", "^postgresql-.*\\.jdbc\\d?\\.jar$", "org.postgresql.Driver"),
      new JdbcDriverDef(Rdbms.ORACLE, "^jdbc:oracle:.*$", "^(ojdbc.*|orai18n)\\.jar$", "oracle.jdbc.driver.OracleDriver"),
      new JdbcDriverDef(Rdbms.MSSQL, "^jdbc:sqlserver:.*$", "^sqljdbc4\\.jar$", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
      new JdbcDriverDef(Rdbms.MSSQL, "^jdbc:jtds:sqlserver:.*$", "^jtds-.*\\.jar$", "net.sourceforge.jtds.jdbc.Driver"),
      new JdbcDriverDef(Rdbms.MYSQL, "^jdbc:mysql:.*$", "^mysql-connector-.*\\.jar$", "com.mysql.jdbc.Driver"),
      new JdbcDriverDef(Rdbms.HSQL2, "^jdbc:hsqldb:.*$", "^hsqldb\\.jar$", "org.hsqldb.jdbc.JDBCDriver")
    )
  );


  private final ImmutableMap<Rdbms,DBErrorRecognizer> myErrorRecognizers =
    ImmutableMap.<Rdbms,DBErrorRecognizer>builder()
      .put(Rdbms.ORACLE, new OraErrorRecognizer())
      .put(Rdbms.UNKNOWN, new UnknownErrorRecognizer())
      .build();



  @NotNull
  @Override
  public DBFacade provide(@NotNull final String connectionString) {
    JdbcDriverDef driverDef = determineDriverDef(connectionString);
    Rdbms rdbms = driverDef != null ? driverDef.rdbms : Rdbms.UNKNOWN;
    Driver driver = obtainDriver(driverDef, connectionString);
    DBErrorRecognizer errorRecognizer = obtainErrorRecognizer(rdbms);
    switch (rdbms) {
      case ORACLE: return new OraFacade(connectionString, driver, errorRecognizer);
      default: throw new DbmsUnsupportedFeatureError("This RDBMS is not supported yet.");
    }
  }


  @NotNull
  protected Driver obtainDriver(@Nullable final JdbcDriverDef driverDef, @NotNull final String connectionString) {
    if (driverDef != null) {
      try {
        // TODO load jar if needed
        Class.forName(driverDef.driverClassName); // to initialize driver's static fields
        return DriverManager.getDriver(connectionString);
      } catch (ClassNotFoundException cnfe) {
        throw new DBDriverError(format("Failed to connect to %s: class %s not found.", driverDef.rdbms, driverDef.driverClassName), cnfe);
      } catch (SQLException sqle) {
        throw new DBDriverError(format("Failed to connect to %s: could not instantiate driver %s.", driverDef.rdbms, driverDef.driverClassName), sqle);
      }
    } else {
      // try to use the DriverManager as is
      try {
        return DriverManager.getDriver(connectionString);
      }
      catch (SQLException sqle) {
        throw new DBDriverError("Failed to connect to an unknown database: could not instantiate driver for given connection string.", sqle);
      }
    }
  }


  @Nullable
  private JdbcDriverDef determineDriverDef(@NotNull final String connectionString) {
    for (JdbcDriverDef def : myDriverDefs) {
      if (matches(connectionString, def.connectionStringPattern)) return def;
    }
    return null;
  }


  @NotNull
  private DBErrorRecognizer obtainErrorRecognizer(@Nullable final Rdbms rdbms) {
    DBErrorRecognizer r = null;
    if (rdbms != null) r = myErrorRecognizers.get(rdbms);
    if (r == null) r = myErrorRecognizers.get(Rdbms.UNKNOWN);
    assert r != null : "The error recognizer for unknown RDBMS must be registered.";
    return r;
  }



}