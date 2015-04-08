package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.jdbc.JdbcDataSource;
import org.jetbrains.jdba.sql.SQL;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseTestDB {

  @NotNull
  public final SQL sql;

  @NotNull
  public final DBFacade facade;


  public static BaseTestDB connect() {
    DBServiceFactory serviceFactory = TestEnvironment.getServiceFactory();
    JdbcDataSource dataSource = new JdbcDataSource(TestEnvironment.getConnectionString(), null, TestEnvironment.getJdbcDriver());
    DBFacade facade = serviceFactory.createFacade(dataSource);
    facade.connect();

    final BaseTestDB testDB;
    String code = facade.rdbms().code;
    if (code.equalsIgnoreCase("Postgre")) {
      testDB = new PostgreTestDB(facade);
    }
    else if (code.equalsIgnoreCase("Oracle")) {
      testDB = new OracleTestDB(facade);
    }
    else {
      testDB = new BaseTestDB(facade);
    }

    return testDB;
  }


  BaseTestDB(@NotNull final DBFacade facade) {
    this.facade = facade;
    this.sql = facade.sql();
  }


}
