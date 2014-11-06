package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.sql.SQLQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing.categories.ForOracle;

import java.util.Date;

import static org.jetbrains.dba.TestDB.FACADE;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Category(ForOracle.class)
public class OraSessionTest {

  @Test
  public void pass_different_parameters() {

    final String createCmd =
      "create table VARIETY_COLUMNS (   \n" +
      "  char_column char(1),           \n" +
      "  varchar_column varchar(80),    \n" +
      "  nchar_column nchar(1),         \n" +
      "  nvarchar_column nvarchar2(80), \n" +
      "  number_column_1 number(9),     \n" +
      "  number_column_2 number(19),    \n" +
      "  number_column_3 number(10,6),  \n" +
      "  number_column_4 number(20,18), \n" +
      "  float_column float(60),        \n" +
      "  date_column date,              \n" +
      "  raw_column raw(16),            \n" +
      "  long_column long,              \n" +
      "  clob_column clob,              \n" +
      "  nclob_column nclob,            \n" +
      "  blob_column blob               \n" +
      ")                                \n";

    final String insertCmd =
      "insert into VARIETY_COLUMNS values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    final Object[] params = new Object[] {
      'C',
      "Some text. This is a text. It's just a simple text, nothing more. Believe me.",
      'Ю',
      "Вы видели медвежонка с балалайкой на велосипеде в сосновом бору под Брянском?",
      12345678,
      1234567890123456789L,
      -1.5f,
      2.718281828459045235360287471352,
      3.1415926535897932384626433832795028841971693993751,
      new Date(),
      new byte[] {1,2,3,4,5,6,7,8,9,0},
      "Long text. This is a long text. If not long enough, read this 10 time more.",
      "Clob. Just a clob.",
      "Просто N' клоп.",
      new byte[] {1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,11,22,33,44,55,66,77,88,99,100}
    };

    ensureNoTable("VARIETY_COLUMNS");
    runCmd(createCmd);

    FACADE.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull DBTransaction tran) {

        tran.command(insertCmd).withParams(params).run();

      }
    });
  }


  private void ensureNoTable(@NotNull final String tableName) {
    boolean tableExists = tableExists(tableName);
    if (tableExists) {
      runCmd("drop table " + tableName + " cascade constraints");
    }
  }

  private boolean tableExists(@NotNull final String tableName) {
    return queryReturnsRows("select 1 from tabs where table_name = ?", tableName);
  }

  private boolean queryReturnsRows(@NotNull final String query, final Object... params) {
    final SQLQuery<String> q = FACADE.sql().query(query, RowsCollectors.oneRow(String.class));

    return FACADE.inTransaction(new InTransaction<Boolean>() {
      @Override
      public Boolean run(@NotNull DBTransaction tran) {
        final String s = tran.query(q).withParams(params).run();
        return s != null && !s.isEmpty();
      }
    });
  }

  private void runCmd(@NotNull final String cmd) {
    FACADE.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {
        session.command(cmd).run();
      }
    });
  }

}
