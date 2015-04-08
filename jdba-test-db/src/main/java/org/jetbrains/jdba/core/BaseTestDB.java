package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.jdbc.JdbcDataSource;
import org.jetbrains.jdba.sql.SQL;
import org.jetbrains.jdba.sql.SQLCommand;
import org.jetbrains.jdba.sql.SQLQuery;

import java.sql.Driver;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseTestDB {

  @NotNull
  public final SQL sql;

  @NotNull
  public final DBFacade facade;


  public static BaseTestDB connect() {
    final DBServiceFactory serviceFactory = TestEnvironment.getServiceFactory();
    final String connectionString = TestEnvironment.getConnectionString();
    final Driver jdbcDriver = TestEnvironment.getJdbcDriver();
    JdbcDataSource dataSource = new JdbcDataSource(connectionString, null,
                                                   jdbcDriver);
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


  //// USEFUL PROCEDURES \\\\

  public void performCommandInTran(@NotNull final String command, final Object... params) {
    SQLCommand cmd = new SQLCommand(command);
    performCommandInTran(cmd, params);
  }

  public void performCommandInTran(@NotNull final SQLCommand command, final Object... params) {
    facade.inTransaction(new InTransactionNoResult() {
      public void run(@NotNull final DBTransaction tran) {

        DBCommandRunner cmd = tran.command(command);
        if (params != null && params.length > 0) cmd.withParams(params);
        cmd.run();

      }
    });
  }

  public void performCommandInSession(@NotNull final String command) {
    SQLCommand cmd = new SQLCommand(command);
    performCommandInSession(cmd);
  }

  public void performCommandInSession(@NotNull final SQLCommand command) {
    facade.inSession(new InSessionNoResult() {
      public void run(@NotNull final DBSession session) {

        session.command(command).run();

      }
    });
  }

  public void performCommandsInSession(@NotNull final String... commands) {
    int n = commands.length;
    if (n == 0) return;

    SQLCommand[] cmds = new SQLCommand[n];
    for (int i = 0; i <= n; i++) {
      cmds[i] = new SQLCommand(commands[i]);
    }

    performCommandsInSession(cmds);
  }

  public void performCommandsInSession(@NotNull final SQLCommand... commands) {
    facade.inSession(new InSessionNoResult() {
      public void run(@NotNull final DBSession session) {

        for (SQLCommand command : commands) {
          session.command(command).run();
        }

      }
    });
  }


  public <R> R performQuery(@NotNull final SQLQuery<R> query, final Object... params) {
    return facade.inTransaction(new InTransaction<R>() {
      public R run(@NotNull final DBTransaction tran) {

        if (params == null || params.length == 0) {
          return tran.query(query).run();
        }
        else {
          return tran.query(query).withParams(params).run();
        }

      }
    });
  }


  public void ensureNoTables(final String... names) {
    final int n = names != null ? names.length : 0;
    if (n == 0) return;

    SQLCommand[] commands = new SQLCommand[n];
    for (int i = 0; i < n; i++)
      commands[i] = new SQLCommand("drop table if exists " + names[i]);

    performCommandsInSession(commands);
  }



}
