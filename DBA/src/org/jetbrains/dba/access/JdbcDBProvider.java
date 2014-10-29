package org.jetbrains.dba.access;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.errors.DbmsUnsupportedFeatureError;

import java.io.File;
import java.sql.Driver;

import static org.jetbrains.dba.KnownRdbms.*;



/**
 * Implements {@link org.jetbrains.dba.access.DBProvider}.
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


  private final ImmutableMap<Rdbms, BaseErrorRecognizer> myErrorRecognizers =
    ImmutableMap.<Rdbms, BaseErrorRecognizer>builder()
      .put(POSTGRE, new PostgreErrorRecognizer())
      .put(ORACLE, new OraErrorRecognizer())
      .put(MSSQL, new MssqlErrorRecognizer())
      .put(MYSQL, new MysqlErrorRecognizer())
      .put(UNKNOWN, new UnknownErrorRecognizer())
      .build();


  @NotNull
  private JdbcDriverSupport myDriverSupport;


  /**
   * Instantiates a new provider.
   * @see #JdbcDBProvider(java.io.File)
   */
  public JdbcDBProvider() {
    myDriverSupport = new JdbcDriverSupport();
  }


  /**
   * Instantiates a new provider and specify the directory where JDBC drivers are placed.
   * @param jdbcDir   directory with JDBC drivers.
   * @see #JdbcDBProvider()
   */
  public JdbcDBProvider(@NotNull File jdbcDir) {
    this();
    addJdbcDriversDir(jdbcDir);
  }



  @NotNull
  @Override
  public DBFacade provide(@NotNull final String connectionString) {
    JdbcDriverDef driverDef = JdbcDriverSupport.determineDriverDef(connectionString);
    Rdbms rdbms = driverDef != null ? driverDef.rdbms : UNKNOWN;
    Driver driver = myDriverSupport.obtainDriver(connectionString);
    BaseErrorRecognizer errorRecognizer = obtainErrorRecognizer(rdbms);
    if (rdbms == POSTGRE) return new PostgreFacade(connectionString, driver, errorRecognizer);
    else if (rdbms == ORACLE) return new OraFacade(connectionString, driver, errorRecognizer);
    else if (rdbms == MSSQL) return new MssqlFacade(connectionString, driver, errorRecognizer);
    else if (rdbms == MYSQL) return new MysqlFacade(connectionString, driver, errorRecognizer);
    else throw new DbmsUnsupportedFeatureError("This RDBMS is not supported yet.", null);
  }



  @NotNull
  private BaseErrorRecognizer obtainErrorRecognizer(@Nullable final Rdbms rdbms) {
    BaseErrorRecognizer r = null;
    if (rdbms != null) r = myErrorRecognizers.get(rdbms);
    if (r == null) r = myErrorRecognizers.get(UNKNOWN);
    assert r != null : "The error recognizer for unknown RDBMS must be registered.";
    return r;
  }



  public void addJdbcDriversDir(@NotNull File dir) {
    myDriverSupport.addJdbcDir(dir);
  }

}