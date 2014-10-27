package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.access.DBFacade;
import org.jetbrains.dba.access.DBSession;
import org.jetbrains.dba.access.InSessionNoResult;
import org.jetbrains.dba.access.JdbcDBProvider;
import org.jetbrains.dba.sql.SQLCommand;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class TestDB {

  /**
   * Test database provider.
   */
  public static final JdbcDBProvider PROVIDER = new JdbcDBProvider(TestEnvironment.TEP.obtainJdbcDriversDir());

  /**
   * Test database facade.
   */
  public static final DBFacade FACADE = PROVIDER.provide(TestEnvironment.TEP.obtainConnectionString());


  static {
    String infoString = "\n"+
                        "JDBA Test Environment\n" +
                        "\tRdbms: %s\n" +
                        "\tConnection: %s\n" +
                        "\tCurrent directory: %s\n";
    System.out.printf(infoString,
                      FACADE.rdbms().toString(),
                      TestEnvironment.TEP.obtainConnectionString(),
                      System.getProperty("user.dir"));
    FACADE.sql().assignResources(TestDB.class);
  }



  //// TEST UTILITIES \\\\

  public static void zapSchema() {
    switch (FACADE.rdbms()) {
      case ORACLE:
        zapOracleSchema();
        break;
      case MSSQL:
        zapMicrosoftSchema();
        break;
      default:
        throw new IllegalStateException("I don't know how to cleanup a schema in "+FACADE.rdbms()+".");
    }
  }


  private static void zapOracleSchema() {
    final SQLCommand zapCommand = FACADE.sql().command("##ora/zap-schema");
    FACADE.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.command(zapCommand).run();

      }
    });
  }


  private static void zapMicrosoftSchema() {

    final SQLCommand dropProcedures = FACADE.sql().command("##mssql/zap-schema:dropProcedures");
    final SQLCommand dropViews = FACADE.sql().command("##mssql/zap-schema:dropViews");
    final SQLCommand dropFunctions = FACADE.sql().command("##mssql/zap-schema:dropFunctions");
    final SQLCommand dropForeignKeys = FACADE.sql().command("##mssql/zap-schema:dropForeignKeys");
    final SQLCommand dropTables = FACADE.sql().command("##mssql/zap-schema:dropTables");

    FACADE.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.command(dropProcedures).run();
        session.command(dropViews).run();
        session.command(dropFunctions).run();
        session.command(dropForeignKeys).run();
        session.command(dropTables).run();

      }
    });
  }



}
