package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.jetbrains.jdba.sql.Scriptum;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
@SuppressWarnings("SpellCheckingInspection")
public class PostgreTestHelperTest extends CommonIntegrationCase {


  enum Kind {
    TYPE,
    CLASS,
    PROC,
    OPERATOR,
  }


  private static final Scriptum ourScriptum = Scriptum.of(PostgreTestHelperTest.class);


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
    test_zap_object("iso2", Kind.TYPE, "create domain iso2 as char(2)");
  }

  @Test
  public void zap_dependent_domain() {
    test_zap_objects("my_domain_1,my_domain_2,my_domain_0", Kind.TYPE,
                     "create domain my_domain_1 as char(3)",
                     "create domain my_domain_2 as my_domain_1 not null",
                     "create domain my_domain_0 as my_domain_2 check (value like '%0%')");
  }

  @Test
  public void zap_table() {
    test_zap_object("my_table", Kind.CLASS, "create table my_table (c1 char(1))");
  }

  @Test
  public void zap_dependent_table() {
    test_zap_objects("master,detail,xdetail", Kind.CLASS,
                     "create table master (id int primary key)",
                     "create table detail (master_id int references master, flag char(1))",
                     "create table xdetail (master_id int references master, xflag char(1))");
  }

  @Test
  public void zap_table_with_inheritance() {
    test_zap_objects("my_face,my_org,my_person", Kind.CLASS,
                     "create table my_face (id int primary key)",
                     "create table my_org (name varchar(60)) inherits (my_face)",
                     "create table my_person (name1 varchar(25), name2 varchar(25)) inherits (my_face)");
  }

  @Test
  public void zap_view() {
    test_zap_object("my_view", Kind.CLASS, "create view my_view as select 01");
  }

  @Test
  public void zap_dependent_view() {
    test_zap_objects("view1,view2,view0", Kind.CLASS,
                     "create view view1 as select 1 as x",
                     "create view view2 as select x as y from view1",
                     "create view view0 as select x * y from view1 cross join view2");
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
  public void zap_overloaden_functions() {
    test_zap_object("over_plus",
                    Kind.PROC,
                    "create or replace function over_plus(x int, y int) returns int as 'select $1 + $2' language SQL",
                    "create or replace function over_plus(z float, t float) returns float as 'select $1 + $2' language SQL");
  }


  @Test
  public void zap_operator_infix() {
    test_zap_object("^^^^",
                    Kind.OPERATOR,
                    "create operator ^^^^ (procedure=power, leftarg=numeric, rightarg=numeric)");
  }

  @Test
  public void zap_operator_prefix() {
    test_zap_object("<|",
                    Kind.OPERATOR,
                    "create operator <| (procedure=trunc, rightarg=numeric)");
  }

  @Test
  public void zap_operator_suffix() {
    test_zap_object("|>",
                    Kind.OPERATOR,
                    "create operator |> (procedure=ceiling, leftarg=numeric)");
  }

  @Test
  public void zap_operator_and_function() {
    test_zap_object("+|+",
                    Kind.OPERATOR,
                    "create or replace function at_plus(x int, y int) returns int as 'select $1 + $2' language SQL",
                    "create operator +|+ (procedure=at_plus, leftarg=int, rightarg=int)");
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

  private void test_zap_objects(final String objectNames,
                                final Kind objectKind,
                                final String... commands) {
    // create objects
    TH.performScript(commands);

    // ensure that we can detect this kind of objects existence
    final String[] names = objectNames.split(",");
    for (String name : names) {
      final String name1 = name.trim();
      assertThat(objectExists(name1, objectKind))
          .describedAs("Object %s %s should exist", objectKind.name().toLowerCase(), name1)
          .isTrue();
    }

    // zap it
    TH.zapSchema();

    // ensure that all the objects are dropped
    for (String name : names) {
      final String name1 = name.trim();
      assertThat(objectExists(name1, objectKind))
          .describedAs("Object %s %s should be dropped!", objectKind.name().toLowerCase(), name1)
          .isFalse();
    }
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
