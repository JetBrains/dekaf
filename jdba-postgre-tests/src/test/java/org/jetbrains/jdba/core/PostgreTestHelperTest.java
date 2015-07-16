package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class PostgreTestHelperTest extends CommonIntegrationCase {


  @BeforeClass
  public static void connect() {
    DB.connect();
    TH.zapSchema();
  }


  @Test
  public void zap_sequence() {
    test_zap_class_object("seq_101", "create sequence seq_101");
  }

  @Test
  public void zap_data_type() {
    test_zap_class_object("my_complex",
                          "create type my_complex as \n" +
                          "(                         \n" +
                          "  re double precision,    \n" +
                          "  im double precision     \n" +
                          ")                         \n");
  }

  @Test
  public void zap_domain() {
    test_zap_type_object("my_domain",
                         "create domain my_domain as char(2)");
  }

  @Test
  public void zap_table() {
    test_zap_class_object("my_table", "create table my_table (c1 char(1))");
  }

  @Test
  public void zap_view() {
    test_zap_class_object("my_view", "create view my_view as select 01");
  }


  private void test_zap_class_object(@NotNull final String name,
                                     @NotNull final String creationCommandText) {
    // create an object
    TH.performCommand(creationCommandText);

    // ensure that we can detect this kind of objects existence
    assertThat(pgClassExists(name)).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the object is dropped
    assertThat(pgClassExists(name)).isFalse();
  }


  private void test_zap_type_object(@NotNull final String name,
                                    @NotNull final String creationCommandText) {
    // create an object
    TH.performCommand(creationCommandText);

    // ensure that we can detect this kind of objects existence
    assertThat(pgTypeExists(name)).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the object is dropped
    assertThat(pgTypeExists(name)).isFalse();
  }



  private static boolean pgClassExists(@NotNull final String name) {
    assert DB != null;

    final String query =
        "select 1                           \n" +
        "from pg_catalog.pg_class C,        \n" +
            "     pg_catalog.pg_namespace N     \n" +
        "where C.relnamespace = N.oid       \n" +
        "  and N.nspname = current_schema() \n" +
        "  and C.relname = ?                \n";

    Boolean exists =
        DB.inSession(new InSession<Boolean>() {
          @Override
          public Boolean run(@NotNull final DBSession session) {
            return session.query(query, Layouts.existence()).withParams(name).run();
          }
        });
    return exists != null && exists;
  }

  private static boolean pgTypeExists(@NotNull final String name) {
    assert DB != null;

    final String query =
        "select 1                          \n" +
        "from pg_catalog.pg_type T,        \n" +
        "     pg_catalog.pg_namespace N    \n" +
        "where T.typnamespace = N.oid      \n" +
        "  and N.nspname = current_schema()\n" +
        "  and T.typname = ?               \n";

    Boolean exists =
        DB.inSession(new InSession<Boolean>() {
          @Override
          public Boolean run(@NotNull final DBSession session) {
            return session.query(query, Layouts.existence()).withParams(name).run();
          }
        });
    return exists != null && exists;
  }


}
