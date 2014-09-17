package org.jetbrains.dba.access;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.errors.DbmsUnsupportedFeatureError;

import java.io.File;
import java.sql.Driver;



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
      .put(Rdbms.POSTGRE, new PostgreErrorRecognizer())
      .put(Rdbms.ORACLE, new OraErrorRecognizer())
      .put(Rdbms.MSSQL, new MssqlErrorRecognizer())
      .put(Rdbms.MYSQL, new MysqlErrorRecognizer())
      .put(Rdbms.UNKNOWN, new UnknownErrorRecognizer())
      .build();


  @NotNull
  private JdbcDriverSupport myDriverSupport = new JdbcDriverSupport();


  @NotNull
  @Override
  public DBFacade provide(@NotNull final String connectionString) {
    JdbcDriverDef driverDef = JdbcDriverSupport.determineDriverDef(connectionString);
    Rdbms rdbms = driverDef != null ? driverDef.rdbms : Rdbms.UNKNOWN;
    Driver driver = myDriverSupport.obtainDriver(connectionString);
    BaseErrorRecognizer errorRecognizer = obtainErrorRecognizer(rdbms);
    switch (rdbms) {
      case POSTGRE: return new PostgreFacade(connectionString, driver, errorRecognizer);
      case ORACLE: return new OraFacade(connectionString, driver, errorRecognizer);
      case MSSQL: return new MssqlFacade(connectionString, driver, errorRecognizer);
      case MYSQL: return new MysqlFacade(connectionString, driver, errorRecognizer);
      default: throw new DbmsUnsupportedFeatureError("This RDBMS is not supported yet.", null);
    }
  }



  @NotNull
  private BaseErrorRecognizer obtainErrorRecognizer(@Nullable final Rdbms rdbms) {
    BaseErrorRecognizer r = null;
    if (rdbms != null) r = myErrorRecognizers.get(rdbms);
    if (r == null) r = myErrorRecognizers.get(Rdbms.UNKNOWN);
    assert r != null : "The error recognizer for unknown RDBMS must be registered.";
    return r;
  }



  public void addJdbcDriversDir(@NotNull File dir) {
    myDriverSupport.addJdbcDir(dir);
  }

}