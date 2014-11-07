package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.access.DBFacade;
import org.jetbrains.dba.access.DBSession;
import org.jetbrains.dba.access.InSessionNoResult;
import org.jetbrains.dba.access.JdbcDBProvider;
import org.jetbrains.dba.sql.SQLCommand;
import org.jetbrains.dba.utils.*;

import static org.jetbrains.dba.KnownRdbms.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class TestDB {

  /**
   * Test database provider.
   */
  public static final JdbcDBProvider PROVIDER = new JdbcDBProvider(true, TestEnvironment.TEP.obtainJdbcDriversDir());

  /**
   * Test database facade.
   */
  public static final DBFacade FACADE = PROVIDER.provide(TestEnvironment.TEP.obtainConnectionString(), null, 1);

  /**
   * Test utils.
   */
  public static final BaseTestUtils UTILS = getUtils(FACADE);


  static {
    String infoString = "\n"+
                        "JDBA Test Environment\n" +
                        "\tRdbms: %s\n" +
                        "\tConnection: %s\n" +
                        "\tCurrent directory: %s\n";
    Rdbms rdbms = FACADE.rdbms();
    System.out.printf(infoString,
                      rdbms.toString(),
                      TestEnvironment.TEP.obtainConnectionString(),
                      System.getProperty("user.dir"));
    FACADE.sql().assignResources(TestDB.class);
  }

  private static BaseTestUtils getUtils(@NotNull final DBFacade facade) {
    Rdbms rdbms = facade.rdbms();
    if (rdbms == POSTGRE) return new PostgreTestUtils(facade);
    if (rdbms == ORACLE) return new OracleTestUtils(facade);
    if (rdbms == MSSQL) return new MicrosoftTestUtils(facade);
    if (rdbms == MYSQL) return new MysqlTestUtils(facade);
    throw new IllegalStateException("Test utils for "+rdbms+" don't exist.");
  }



  //// TEST UTILITIES \\\\

  public static void zapSchema() {
    Rdbms rdbms = FACADE.rdbms();
    if (rdbms == ORACLE) zapOracleSchema();
    else if (rdbms == MSSQL) zapMicrosoftSchema();
    else throw new IllegalStateException("I don't know how to cleanup a schema in " + FACADE.rdbms() + ".");
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
