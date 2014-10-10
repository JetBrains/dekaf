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
   * Provides with some options how to connect to a test database.
   */
  private static final TestEnvironmentProvider TEP = new TestEnvironmentProvider();

  /**
   * Test database provider.
   */
  public static final JdbcDBProvider PROVIDER = new JdbcDBProvider(TEP.obtainJdbcDriversDir());

  /**
   * Test database facade.
   */
  public static final DBFacade FACADE = PROVIDER.provide(TEP.obtainConnectionString());


  static {
    String infoString = "\n"+
                        "JDBA Test Environment\n" +
                        "\tRdbms: %s\n" +
                        "\tConnection: %s\n" +
                        "\tCurrent directory: %s\n";
    System.out.printf(infoString,
                      FACADE.rdbms().toString(),
                      TEP.obtainConnectionString(),
                      System.getProperty("user.dir"));
  }



  //// TEST UTILITIES \\\\

  public static void zapSchema() {
    switch (FACADE.rdbms()) {
      case ORACLE:
        zapOracleSchema();
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



}
