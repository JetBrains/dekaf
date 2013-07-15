package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.DBFacade;
import org.jetbrains.dba.DBProvider;
import org.jetbrains.dba.JdbcDBProvider;
import org.jetbrains.dba.Rdbms;



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
