package org.jetbrains.jdba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBFacade;
import org.jetbrains.jdba.core.DBSession;
import org.jetbrains.jdba.core.InSessionNoResult;
import org.jetbrains.jdba.jdbc.JdbcDBProvider;
import org.jetbrains.jdba.sql.SQLCommand;
import org.jetbrains.jdba.utils.*;



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
    if (rdbms == org.jetbrains.jdba.rdbms.postgre.Postgre.RDBMS) return new PostgreTestUtils(facade);
    if (rdbms == org.jetbrains.jdba.rdbms.oracle.Oracle.RDBMS) return new OracleTestUtils(facade);
    if (rdbms == org.jetbrains.jdba.rdbms.microsoft.MicrosoftSQL.RDBMS) return new MicrosoftTestUtils(facade);
    if (rdbms == org.jetbrains.jdba.rdbms.mysql.MySQL.RDBMS) return new MysqlTestUtils(facade);
    throw new IllegalStateException("Test utils for "+rdbms+" don't exist.");
  }



  //// TEST UTILITIES \\\\

  public static void zapSchema() {
    Rdbms rdbms = FACADE.rdbms();
    if (rdbms == org.jetbrains.jdba.rdbms.oracle.Oracle.RDBMS) zapOracleSchema();
    else if (rdbms == org.jetbrains.jdba.rdbms.microsoft.MicrosoftSQL.RDBMS) zapMicrosoftSchema();
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
