package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.DBFacade;
import org.jetbrains.jdba.core.DBProvider;
import org.jetbrains.jdba.core.DBServiceFactory;
import org.jetbrains.jdba.core.errors.DBFactoryError;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;



/**
 * Implements {@link org.jetbrains.jdba.core.DBProvider}.
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


  @NotNull
  private CopyOnWriteArrayList<DBServiceFactory> myServiceFactories;

  @NotNull
  private JdbcDriverSupport myDriverSupport;


  /**
   * Instantiates a new provider.
   * @see #JdbcDBProvider(boolean, java.io.File)
   */
  public JdbcDBProvider() {
    myServiceFactories = new CopyOnWriteArrayList<DBServiceFactory>();
    myDriverSupport = new JdbcDriverSupport();
  }


  /**
   * Instantiates a new provider and specify the directory where JDBC drivers are placed.
   * @param jdbcDir   directory with JDBC drivers.
   * @see #JdbcDBProvider()
   */
  public JdbcDBProvider(boolean registerKnownRDBMS, @NotNull File jdbcDir) {
    this();
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