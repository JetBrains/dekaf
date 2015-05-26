package org.jetbrains.jdba.jdbc;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



@FixMethodOrder(MethodSorters.JVM)
public class JdbcIntermediateSeanceSimpleTest extends BaseIntermediateHyperSonicCase {

  @Test
  public void simple_DDL_command() {
    JdbcIntermediateSession session = openSession();
    JdbcIntermediateSeance seance = session.openSeance("create table T1 (F1 integer)", null);
    seance.execute();
    seance.close();
    session.close();
  }


  @Test
  public void number_of_affected_rows() {
    JdbcIntermediateSession session = openSession();

    JdbcIntermediateSeance seance1 = session.openSeance("create table T3 (X integer)", null);
    seance1.execute();
    seance1.close();

    JdbcIntermediateSeance seance2 = session.openSeance("insert into T3 values (11), (22), (33)", null);
    seance2.execute();
    assertThat(seance2.getAffectedRowsCount()).isEqualTo(3);
    seance2.close();

    session.close();
  }


}