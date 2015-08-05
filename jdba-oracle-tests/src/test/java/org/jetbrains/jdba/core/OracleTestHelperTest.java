package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.jetbrains.jdba.sql.Scriptum;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class OracleTestHelperTest extends CommonIntegrationCase {

  private static final Scriptum ourScriptum =
      Scriptum.of(OracleTestHelperTest.class);



  //// ZAP \\\\

  @BeforeClass
  public static void connect() {
    DB.connect();
    TH.zapSchema();
  }


  @Test
  public void zap_sequence() {
    // create a sequence
    TH.performCommand("create sequence SEQ_101");

    // ensure that we can detect the sequence existence
    assertThat(objectExists("SEQ_101")).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the sequence is dropped
    assertThat(objectExists("SEQ_101")).isFalse();
  }


  @Test
  public void zap_synonym() {
    // create a synonym
    TH.performCommand("create synonym MY_DUAL for sys.dual");

    // ensure that we can detect the synonym existence
    assertThat(objectExists("MY_DUAL")).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the synonym is dropped
    assertThat(objectExists("MY_DUAL")).isFalse();
  }


  @Test
  public void zap_operator() {
    // create function and operator
    TH.performScript(ourScriptum, "CreateOperator");

    // ensure that we can detect them
    assertThat(objectExists("EQ_F")).isTrue();
    assertThat(objectExists("EQ_OP")).isTrue();

    // zap it
    TH.zapSchema();

    // verify
    assertThat(objectExists("EQ_OP")).isFalse();
    assertThat(objectExists("EQ_F")).isFalse();
  }


  @Test
  public void zap_mater_view() {
    // create function and operator
    TH.performScript(ourScriptum, "CreateMaterView");

    // ensure that we can detect a materialized view
    assertThat(objectExists("X_ORDER_STAT")).isTrue();

    // zap it
    TH.zapSchema();

    // verify
    assertThat(objectExists("X_ORDER_STAT")).isFalse();
  }


  private static boolean objectExists(@NotNull final String sequenceName) {
    assert DB != null;

    final String query =
        "select 1 from user_objects where object_name = ?";

    Boolean exists =
        DB.inSession(new InSession<Boolean>() {
          @Override
          public Boolean run(@NotNull final DBSession session) {
            return session.query(query, Layouts.existence()).withParams(sequenceName).run();
          }
        });
    return exists != null && exists;
  }


}
