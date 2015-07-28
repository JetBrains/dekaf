package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.jetbrains.jdba.sql.Scriptum;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class PostgreTestHelperTest extends CommonIntegrationCase {


  enum Kind {
    TYPE,
    CLASS,
    PROC;
  }


  private static Scriptum ourScriptum = Scriptum.of(PostgreTestHelperTest.class);


  @BeforeClass
  public static void connect() {
    DB.connect();
    TH.zapSchema();
  }


  @Test
  public void zap_sequence() {
    test_zap_object("seq_101", Kind.CLASS, "create sequence seq_101");
  }

  @Test
  public void zap_data_type() {
    test_zap_object("my_complex",
                    Kind.TYPE,
                    "create type my_complex as \n" +
                    "(                         \n" +
                    "  re double precision,    \n" +
                    "  im double precision     \n" +
                    ")                         \n");
  }

  @Test
  public void zap_enum() {
    test_zap_object("mood", Kind.TYPE, "create type mood as enum ('sad', 'ok', 'happy')");
  }

  @Test
  public void zap_domain() {
    test_zap_object("my_domain", Kind.TYPE, "create domain my_domain as char(2)");
  }

  @Test
  public void zap_table() {
    test_zap_object("my_table", Kind.CLASS, "create table my_table (c1 char(1))");
  }

  @Test
  public void zap_table_with_inheritance() {
    TH.zapSchema();
    TH.performScript("create table my_face (id int primary key)",
                     "create table my_org (name varchar(60)) inherits (my_face)",
                     "create table my_person (name1 varchar(25), name2 varchar(25)) inherits (my_face)");

    assertThat(objectExists("my_face", Kind.CLASS)).isTrue();
    assertThat(objectExists("my_org", Kind.CLASS)).isTrue();
    assertThat(objectExists("my_person", Kind.CLASS)).isTrue();

    TH.zapSchema();

    assertThat(objectExists("my_face", Kind.CLASS)).isFalse();
    assertThat(objectExists("my_org", Kind.CLASS)).isFalse();
    assertThat(objectExists("my_person", Kind.CLASS)).isFalse();
  }

  @Test
  public void zap_view() {
    test_zap_object("my_view", Kind.CLASS, "create view my_view as select 01");
  }

  @Test
  public void zap_simple_function() {
    test_zap_object("simple_f", Kind.PROC, "create or replace function simple_f() returns int as 'select 44' language SQL");
  }

  @Test
  public void zap_simple_function_with_param() {
    test_zap_object("simple_f1", Kind.PROC, "create or replace function simple_f1(x int) returns int as 'select $1 * $1' language SQL");
  }

  @Test
  public void zap_overriden_functions() {
    test_zap_object("over_plus",
                    Kind.PROC,
                    "create or replace function over_plus(x int, y int) returns int as 'select $1 + $2' language SQL",
                    "create or replace function over_plus(z float, t float) returns float as 'select $1 + $2' language SQL");
  }



  private void test_zap_object(@NotNull final String name,
                               @NotNull final Kind kind,
                               @NotNull final String... creationCommands) {
    // create an object
    TH.performScript(creationCommands);

    // ensure that we can detect this kind of objects existence
    assertThat(objectExists(name, kind)).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the object is dropped
    assertThat(objectExists(name, kind)).isFalse();
  }


  private static boolean objectExists(@NotNull final String name,
                                      @NotNull final Kind kind) {
    assert DB != null;

    final SqlQuery<Boolean> query =
        ourScriptum.query("existence_of_" + kind.name().toLowerCase(Locale.ROOT), Layouts.existence());

    Boolean exists =
        DB.inSession(new InSession<Boolean>() {
          @Override
          public Boolean run(@NotNull final DBSession session) {
            return session.query(query).withParams(name).run();
          }
        });
    return exists != null && exists;
  }


}
