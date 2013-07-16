package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.access.DBFacade;
import org.jetbrains.dba.access.DBProvider;
import org.jetbrains.dba.access.JdbcDBProvider;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class TestDB {

  @NotNull
  static final DBProvider ourProvider;

  @NotNull
  static final Rdbms ourRdbms;

  @NotNull
  static final DBFacade ourFacade;


  static {
    ourProvider = new JdbcDBProvider();
    String connectionString = System.getProperty("dbConnectionString");
    if (connectionString == null) throw new RuntimeException("The property dbConnectionString is not specified!");
    ourFacade = ourProvider.provide(connectionString);
    ourRdbms = ourFacade.getDbms();
  }


}
