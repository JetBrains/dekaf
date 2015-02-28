package org.jetbrains.dba.access;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.errors.DBFactoryError;
import org.jetbrains.dba.rdbms.microsoft.MicrosoftServiceFactory;
import org.jetbrains.dba.rdbms.mysql.MysqlServiceFactory;
import org.jetbrains.dba.rdbms.oracle.OracleServiceFactory;
import org.jetbrains.dba.rdbms.postgre.PostgreServiceFactory;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;



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


  private static final ImmutableList<Class<? extends DBServiceFactory>> KNOWN_SERVICE_FACTORIES =
    ImmutableList.of(
      PostgreServiceFactory.class,
      OracleServiceFactory.class,
      MicrosoftServiceFactory.class,
      MysqlServiceFactory.class
    );


  @NotNull
  private CopyOnWriteArrayList<DBServiceFactory> myServiceFactories;

  @NotNull
  private JdbcDriverSupport myDriverSupport;


  /**
   * Instantiates a new provider.
   * @see #JdbcDBProvider(boolean, java.io.File)
   */
  public JdbcDBProvider(boolean registerKnownRDBMS) {
    myServiceFactories = new CopyOnWriteArrayList<DBServiceFactory>();
    myDriverSupport = new JdbcDriverSupport();

    if (registerKnownRDBMS) {
      for (Class<? extends DBServiceFactory> factoryClass : KNOWN_SERVICE_FACTORIES) {
        registerServiceFactory(factoryClass);
      }
    }
  }


  /**
   * Instantiates a new provider and specify the directory where JDBC drivers are placed.
   * @param jdbcDir   directory with JDBC drivers.
   * @see #JdbcDBProvider(boolean)
   */
  public JdbcDBProvider(boolean registerKnownRDBMS, @NotNull File jdbcDir) {
    this(registerKnownRDBMS);
    addJdbcDriversDir(jdbcDir);
  }


  public void registerServiceFactory(@NotNull final Class<? extends DBServiceFactory> factoryClass) {
    final DBServiceFactory factory;
    try {
      factory = factoryClass.newInstance();
    }
    catch (Exception e) {
      String msg = String.format("Failed to instantiate DB service factory class %s: %s: %s",
                                 factoryClass.getSimpleName(), e.getClass().getSimpleName(), e.getMessage());
      throw new DBFactoryError(msg, e);
    }

    Rdbms rdbms = factory.rdbms();
    DBServiceFactory alreadyRegistered = findServiceFactory(rdbms);
    if (alreadyRegistered != null) throw new IllegalStateException("The service factory for "+rdbms+" already registered.");
    myServiceFactories.add(factory);
  }


  @Nullable
  public DBServiceFactory findServiceFactory(@NotNull final Rdbms rdbms) {
    for (DBServiceFactory factory : myServiceFactories) {
      if (factory.rdbms().equals(rdbms)) return factory;
    }

    return null;
  }

  @NotNull
  public DBServiceFactory determineServiceFactory(@NotNull final String connectionString) {
    for (DBServiceFactory factory : myServiceFactories) {
      Matcher m = factory.connectionStringPattern().matcher(connectionString);
      if (m.matches()) return factory;
    }

    throw new IllegalArgumentException("Unrecognizable connection string");
  }


  public void addJdbcDriversDir(@NotNull File dir) {
    myDriverSupport.addJdbcDir(dir);
  }



  @NotNull
  @Override
  public DBFacade provide(@NotNull final String connectionString,
                          @Nullable final Properties connectionProperties,
                          int connectionsLimit) {
    DBServiceFactory factory = determineServiceFactory(connectionString);
    JdbcDataSource dataSource = myDriverSupport.createDataSource(connectionString, connectionProperties);
    return provide(factory, dataSource, connectionsLimit);
  }


  @NotNull
  @Override
  public DBFacade provide(@NotNull final Rdbms rdbms,
                          @NotNull final DataSource dataSource,
                          int connectionsLimit) {
    DBServiceFactory factory = findServiceFactory(rdbms);
    if (factory == null) throw new IllegalStateException("The service factory for "+rdbms+" is not registered");
    return provide(factory, dataSource, connectionsLimit);
  }


  private DBFacade provide(@NotNull final DBServiceFactory factory,
                           @NotNull final DataSource dataSource,
                           int connectionsLimit) {
    DBFacade facade = factory.createFacade(dataSource);
    facade.setSessionsLimit(connectionsLimit);
    if (connectionsLimit >= 1) facade.connect();
    return facade;
  }
}