package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.access.DBFacade;
import org.jetbrains.dba.access.DBSession;
import org.jetbrains.dba.access.InSessionNoResult;
import org.jetbrains.dba.access.JdbcDBProvider;
import org.jetbrains.dba.sql.OraSQL;
import org.jetbrains.dba.sql.SQL;
import org.jetbrains.dba.sql.SQLCommand;
import org.jetbrains.dba.sql.SQLScript;

import java.io.File;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class TestDB {

  private static final String JDBC_DRIVERS_PATH_VAR = "jdbc.drivers.path";

  public static final JdbcDBProvider ourProvider = new JdbcDBProvider();

  @NotNull
  public static final Rdbms ourRdbms;

  @NotNull
  public static final DBFacade ourDB;

  @NotNull
  public static final SQL ourSQL;


  static {
    System.setProperty("java.awt.headless", "true");

    TestConnectionStringProvider csp = new TestConnectionStringProvider();
    String connectionString = csp.findConnectionString();
    if (connectionString == null) throw new IllegalStateException("Cannot find how to connect to a test database");

    String jdbcPath = csp.getVar(JDBC_DRIVERS_PATH_VAR);
    if (jdbcPath != null) {
      File jdbcDir = new File(jdbcPath);
      // TODO check whether this dir presents
      ourProvider.addJdbcDriversDir(jdbcDir);
    }

    ourDB = ourProvider.provide(connectionString);
    ourRdbms = ourDB.getDbms();

    // TODO create the instance depends on the current RDBMS
    ourSQL = new SQL();
  }


  @NotNull
  public static DBFacade provide() {
    return ourDB;
  }


  //// TEST SQL \\\\

  static final SQL ourOracleSQL;

  static {
    ourOracleSQL = new OraSQL();
    ourOracleSQL.assignResources(TestDB.class, "ora");
  }



  //// TEST UTILITIES \\\\

  public static void performCommand(@NotNull final String commandText) {
    SQLCommand command = ourSQL.command(commandText);
    performCommand(command);
  }

  public static void performCommand(@NotNull final SQLCommand command) {
    ourDB.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.command(command).run();

      }
    });
  }

  public static void performScript(@NotNull final String scriptText) {
    SQLScript script = ourSQL.script(scriptText);
    performScript(script);
  }

  public static void performScript(@NotNull final SQLScript script) {
    ourDB.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.script(script).run();

      }
    });
  }


  public static void zapSchema() {
    switch (ourRdbms) {
      case ORACLE:
        zapOracleSchema();
        break;
      default:
        throw new IllegalStateException("I don't know how to cleanup a "+ourRdbms+" schema.");
    }
  }

  private static void zapOracleSchema() {
    final SQLCommand zapCommand = ourOracleSQL.command("##zap-schema");
    ourDB.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.command(zapCommand).run();

      }
    });
  }


}
