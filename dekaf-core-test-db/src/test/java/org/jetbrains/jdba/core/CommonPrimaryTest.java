package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.Driver;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public abstract class CommonPrimaryTest extends CommonIntegrationCase {

  @Test
  public void connect_disconnect() {
    assert DB != null;

    DB.connect();
    assertThat(DB.isConnected()).isTrue();

    DB.disconnect();
    assertThat(DB.isConnected()).isFalse();
  }


  @Test
  public void ping() {
    assert DB != null;

    DB.connect();

    final DBLeasedSession session = DB.leaseSession();
    session.ping();
    session.close();

    DB.disconnect();
  }


  @Test
  public void select_1_in_session() {
    assert DB != null;

    DB.connect();
    DB.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        final Integer v = session.query("select 1 " + fromSingleRowTable(),
                                          Layouts.singleOf(Integer.class))
                                   .run();
        assertThat(v).isNotNull()
                     .isEqualTo(1);

      }
    });
  }

  @Test
  public void select_1_in_transaction() {
    assert DB != null;

    DB.connect();
    DB.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        final Integer v = tran.query("select 1 " + fromSingleRowTable(),
                                        Layouts.singleOf(Integer.class))
                                 .run();
        assertThat(v).isNotNull()
                     .isEqualTo(1);

      }
    });
  }

  @NotNull
  protected String fromSingleRowTable() {
    return "";
  }


  @Test
  public void getConnectionInfo_versions() {
    ConnectionInfo info = DB.getConnectionInfo();
    assertThat(info.serverVersion.isOrGreater(1));
    assertThat(info.driverVersion.isOrGreater(1));
  }


  @Test
  public void zapSchema_basic() {
    DB.connect();
    TH.ensureNoTableOrView("T1", "V1");

    // create a table and a view
    TH.performScript(
        "create table T1 (F1 char(1))",
        "create view V1 as select * from T1"
    );

    // zap schema
    TH.zapSchema();

    // TODO verify that no tables and views
  }

  @Test
  public void zapSchema_foreignKeys() {
    DB.connect();
    TH.ensureNoTableOrView("T1", "T2", "T3");

    // create a table and a view
    TH.performScript(
        "create table T1 (X char(1) primary key)",
        "create table T2 (Y char(1))",
        "create table T3 (Z char(1) primary key)",
        "alter table T2 add foreign key (Y) references T1",
        "alter table T2 add foreign key (Y) references T3"
    );

    // zap schema
    TH.zapSchema();

    // TODO verify that no tables
  }

  @Test
  public void get_driver() {
    Driver driver =
        DB.getSpecificService(Driver.class, ImplementationAccessibleService.Names.JDBC_DRIVER);
    assertThat(driver).isNotNull();
  }

}
