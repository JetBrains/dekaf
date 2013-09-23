package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.access.DBFacade;
import org.jetbrains.dba.access.DBSession;
import org.jetbrains.dba.access.InSessionNoResult;
import org.jetbrains.dba.access.JdbcDBProvider;
import org.jetbrains.dba.sql.OraSQL;
import org.jetbrains.dba.sql.SQL;
import org.jetbrains.dba.sql.SQLCommand;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class TestDB {

  static final String PRIMARY_RDBMS_START_PARAMETER = "db_test_with";
  static final String RDBMS_URL_START_PARAMETER = "db_test_@@@_url";

  @NotNull
  public static final Rdbms PRIMARY_RDBMS;


  public static final JdbcDBProvider PROVIDER = new JdbcDBProvider();


  static {
    System.setProperty("java.awt.headless", "true");

    String thePrimaryRdbmsCode = getStartParameter(PRIMARY_RDBMS_START_PARAMETER);
    if (thePrimaryRdbmsCode != null && !thePrimaryRdbmsCode.isEmpty()) {
      thePrimaryRdbmsCode = (thePrimaryRdbmsCode.toLowerCase()+"  ").substring(0, 2);
      if (thePrimaryRdbmsCode.equals("or")) PRIMARY_RDBMS = Rdbms.ORACLE;
      else if (thePrimaryRdbmsCode.equals("po") || thePrimaryRdbmsCode.equals("pg")) PRIMARY_RDBMS = Rdbms.POSTGRE;
      else if (thePrimaryRdbmsCode.equals("ms")) PRIMARY_RDBMS = Rdbms.MSSQL;
      else if (thePrimaryRdbmsCode.equals("my")) PRIMARY_RDBMS = Rdbms.MYSQL;
      else PRIMARY_RDBMS = Rdbms.UNKNOWN;
    }
    else {
      Rdbms theRdbms = Rdbms.UNKNOWN;
      for (Rdbms rdbms: new Rdbms[] { Rdbms.ORACLE, Rdbms.POSTGRE, Rdbms.MSSQL, Rdbms.MYSQL, Rdbms.HSQL2 }) {
        String url = getURL(rdbms);
        if (url != null) {
          theRdbms = rdbms;
          break;
        }
      }
      PRIMARY_RDBMS = theRdbms;
    }
  }


  @NotNull
  public static DBFacade provide() {
    if (PRIMARY_RDBMS != Rdbms.UNKNOWN) {
      return provide(PRIMARY_RDBMS);
    }
    else {
      throw new IllegalStateException("The primary RDBMS is not specified correctly");
    }
  }


  private static DBFacade provide(@NotNull final Rdbms rdbms) {
    String url = getURL(rdbms);
    if (url == null) throw new IllegalArgumentException("Unknown how to connect to " + rdbms.name());
    return PROVIDER.provide(url);
  }


  @Nullable
  private static String getURL(@NotNull final Rdbms rdbms) {
    String paramName = RDBMS_URL_START_PARAMETER.replace("@@@", rdbms.shortName.toLowerCase());
    return getStartParameter(paramName);
  }


  //// TEST SQL \\\\

  static final SQL ourOracleSQL;

  static {
    ourOracleSQL = new OraSQL();
    ourOracleSQL.assignResources(TestDB.class, "ora");
  }

  //// TEST UTILITIES \\\\

  public static void zapSchema(@NotNull final DBFacade facade) {
    final Rdbms rdbms = facade.getDbms();
    switch (rdbms) {
      case ORACLE:
        zapOracleSchema(facade);
        break;
      default:
        throw new IllegalStateException("I don't know how to cleanup a "+rdbms+" schema.");
    }
  }


  private static void zapOracleSchema(@NotNull final DBFacade facade) {
    final SQLCommand zapCommand = ourOracleSQL.command("##zap-schema");
    facade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.command(zapCommand).run();

      }
    });
  }




  //// UTILITY FUNCTIONS \\\\

  @Nullable
  static String getStartParameter(@NotNull final String name) {
    String value = System.getProperty(name);
    if (value == null) value = System.getenv(name);
    if (value == null) value = System.getenv(name.toUpperCase());
    return value;
  }


}
