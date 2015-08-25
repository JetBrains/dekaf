package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MssqlTestHelperTest extends CommonIntegrationCase {


  @BeforeClass
  public static void connect() {
    DB.connect();
    TH.zapSchema();
  }


  @Test
  public void ensure_no_table() {
    TH.performCommand("create table Tab1(Col1 int)");
    assertThat(objectExists("Tab1")).isTrue();
    TH.ensureNoTableOrView("Tab1");
    assertThat(objectExists("Tab1")).isFalse();
  }

  @Test
  public void ensure_no_table_fk() {
    TH.performScript("create table T1(Id int primary key)",
                     "create table T2(Id int primary key references T1)",
                     "alter table T1 add foreign key (Id) references T2");
    assertThat(objectExists("T1")).isTrue();
    assertThat(objectExists("T2")).isTrue();
    TH.ensureNoTableOrView("T1","T2");
    assertThat(objectExists("T1")).isFalse();
    assertThat(objectExists("T2")).isFalse();
  }

  @Test
  public void zap_table() {
    TH.performCommand("create table Tab1(Col1 int)");
    assertThat(objectExists("Tab1")).isTrue();
    TH.zapSchema();
    assertThat(objectExists("Tab1")).isFalse();
  }

  @Test
  public void zap_synonym() {
    TH.performScript("create table Tab1(Col1 int)",
                     "create synonym Syn1 for Tab1");
    assertThat(objectExists("Syn1")).isTrue();
    TH.zapSchema();
    assertThat(objectExists("Syn1")).isFalse();
  }


  private static final SqlQuery<Boolean> ourObjectExistsQuery =
      new SqlQuery<Boolean>("select 1 from sysobjects where name = ?", Layouts.existence());

  private static boolean objectExists(@NotNull final String sequenceName) {
    assert DB != null;

    Boolean exists =
        DB.inSession(new InSession<Boolean>() {
          @Override
          public Boolean run(@NotNull final DBSession session) {
            return session.query(ourObjectExistsQuery).withParams(sequenceName).run();
          }
        });
    return exists != null && exists;
  }


}
