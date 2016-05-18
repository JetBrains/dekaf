package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.CommonIntegrationCase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class CommonPrimaryTest extends CommonIntegrationCase {

  @Test
  public void connect() {
    assert DB != null;

    DB.connect();
    assertThat(DB.isConnected()).isTrue();

    ConnectionInfo info = DB.getConnectionInfo();
    System.out.println("Connection info:");
    System.out.println("\tRDBMS: " + info.rdbmsName);
    System.out.println("\tDatabase: " + info.databaseName);
    System.out.println("\tSchema: " + info.schemaName);
    System.out.println("\tUser: " + info.userName);
    System.out.println("\tServer version: " + info.serverVersion);
    System.out.println("\tDriver version: " + info.driverVersion);
  }

  @Test
  public void disconnect() {
    assert DB != null;

    DB.disconnect();
    assertThat(DB.isConnected()).isFalse();
  }

  @Test
  public void access_metaData() {
    DB.connect();

    final StringBuilder b = new StringBuilder(240);
    b.append("JDBC DatabaseMetaData:\n");

    DB.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        DatabaseMetaData md =
            session.getSpecificService(DatabaseMetaData.class,
                                       ImplementationAccessibleService.Names.JDBC_METADATA);
        assertThat(md).isNotNull();

        try {
          b.append("\tDatabaseProductName: ").append(md.getDatabaseProductName()).append('\n');
          b.append("\tDriverName: ").append(md.getDriverName()).append('\n');
          b.append("\tUserName: ").append(md.getUserName()).append('\n');
          b.append("\tDatabaseProductVersion: ").append(md.getDatabaseProductVersion()).append('\n');
          b.append("\tDriverVersion: ").append(md.getDriverVersion()).append('\n');
          b.append("\tExtraNameCharacters: ").append(md.getExtraNameCharacters()).append('\n');
          b.append("\tIdentifierQuoteString: ").append(md.getIdentifierQuoteString()).append('\n');
        }
        catch (SQLException e) {
          throw new RuntimeException(e);
        }

      }
    });

    System.out.println(b.toString());
  }

  @Test
  public void getConnectionInfo_versions() {
    DB.connect();
    ConnectionInfo info = DB.getConnectionInfo();
    assertThat(info.serverVersion.isOrGreater(1));
    assertThat(info.driverVersion.isOrGreater(1));
  }

  @Test
  public void getConnectionInfo_rdbms() {
    DB.connect();
    ConnectionInfo info = DB.getConnectionInfo();
    assertThat(info.rdbmsName).isNotEmpty();
  }

  @Test
  public void getConnectionInfo_database() {
    DB.connect();
    ConnectionInfo info = DB.getConnectionInfo();
    assertThat(info.databaseName).isNotEmpty();
  }

  @Test
  public void getConnectionInfo_schema() {
    DB.connect();
    ConnectionInfo info = DB.getConnectionInfo();
    assertThat(info.schemaName).isNotEmpty();
  }

  @Test
  public void getConnectionInfo_user() {
    DB.connect();
    ConnectionInfo info = DB.getConnectionInfo();
    assertThat(info.userName).isNotEmpty();
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

        final Integer v = session.query("select 1 " + TH.fromSingleRowTable(),
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

        final Integer v = tran.query("select 1 " + TH.fromSingleRowTable(),
                                        Layouts.singleOf(Integer.class))
                                 .run();
        assertThat(v).isNotNull()
                     .isEqualTo(1);

      }
    });
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
        "create table T1 (X char(1) not null primary key)",
        "create table T2 (Y char(1) not null)",
        "create table T3 (Z char(1) not null primary key)",
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
