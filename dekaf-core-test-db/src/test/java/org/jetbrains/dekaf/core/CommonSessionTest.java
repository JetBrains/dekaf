package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.CommonIntegrationCase;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public abstract class CommonSessionTest extends CommonIntegrationCase {

  @Before
  public void connect() throws Exception {
    DB.connect();
  }


  @Test
  public void transaction_commit() {
    TH.ensureNoTableOrView("Tab_1");
    TH.performCommand("create table Tab_1 (C1 char(1))");
    TH.performCommand("insert into Tab_1 values ('A')");

    DBLeasedSession session = DB.leaseSession();
    try {
      assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(1);

      session.beginTransaction();

      session.command("insert into Tab_1 values ('B')").run();

      assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(2);

      session.commit();

      assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(2);
    }
    finally {
      session.close();
    }
  }


  @Test
  public void transaction_rollback() {
    TH.ensureNoTableOrView("Tab_1");
    TH.performCommand("create table Tab_1 (C1 char(1) not null)");
    TH.performCommand("insert into Tab_1 values ('A')");

    DBLeasedSession session = DB.leaseSession();
    try {
      assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(1);

      session.beginTransaction();

      session.command("insert into Tab_1 values ('B')").run();

      assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(2);

      session.rollback();

      assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(1);
    }
    finally {
      session.close();
    }
  }


  @Test
  public void access_metadata() {
    DB.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        DatabaseMetaData md =
            session.getSpecificService(DatabaseMetaData.class,
                                       ImplementationAccessibleService.Names.JDBC_METADATA);

        assertThat(md).isNotNull();

        String driverName = null;
        try {
          driverName = md.getDriverName();
        }
        catch (SQLException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
        assertThat(driverName).isNotNull();

      }
    });
  }


}
