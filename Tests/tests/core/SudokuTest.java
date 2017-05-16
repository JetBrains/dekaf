package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.CommonIntegrationCase;
import org.jetbrains.dekaf.sql.Scriptum;
import org.jetbrains.dekaf.sql.SqlQuery;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.core.Layouts.arrayOf;
import static org.jetbrains.dekaf.core.Layouts.rowOf;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class SudokuTest extends CommonIntegrationCase {

  private Scriptum myScriptum = Scriptum.of(SudokuTest.class);

  @Before
  public void createTables() {
    DB.connect();
    if (TH.countTableRows("sudoku") >= 1) return;
    TH.performCommand(myScriptum, "create-table-sudoku");
    TH.performCommand(myScriptum, "populate-table-sudoku");
  }


  @Test
  public void query_single_sudoku() {
    final SqlQuery<byte[][]> query =
        myScriptum.query("retrieve-one-sudoku", Layouts.singleOf(byte[][].class));

    byte[][] rebus =
        DB.inSession(session -> session.query(query).run());

    assertThat(rebus).hasSize(3);
    assertThat(rebus[0]).hasSize(9);
    assertThat(rebus[1]).hasSize(9);
    assertThat(rebus[2]).hasSize(9);
  }


  @Test
  public void query_couple_of_sudoku() {
    final SqlQuery<byte[][][]> query =
        myScriptum.query("retrieve-couple-of-sudoku", rowOf(arrayOf(2, byte[][].class)));

    byte[][][] puzzle =
        DB.inSession(session -> session.query(query).run());

    assertThat(puzzle).hasSize(2);
    assertThat(puzzle[0]).hasSize(3);
    assertThat(puzzle[0][0]).hasSize(9);
    assertThat(puzzle[1]).hasSize(3);
    assertThat(puzzle[1][0]).hasSize(9);
  }


}
