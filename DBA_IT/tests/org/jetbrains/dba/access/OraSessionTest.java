package org.jetbrains.dba.access;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.DBTestCase;
import org.jetbrains.dba.sql.SQL;
import org.jetbrains.dba.sql.SQLQuery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.testng.Assert.assertEquals;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OraSessionTest extends DBTestCase {

  @BeforeMethod
  public void setUp() throws Exception {


  }


  private final SQL sql = new SQL();

  @Test
  public void pass_string_array() {
    //final String queryText = "select * from table(sys.txname_array('A', 'B', 'C', 'D'))";
    final String queryText = "select * from table(?)";
    final SQLQuery<List<String>> query = sql.query(queryText, RowsCollectors.list(String.class));
    final Collection<String> stringArray = ImmutableList.of("A", "B", "C", "D");

    myFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull DBTransaction tran) {

        final List<String> result = tran.query(query).withParams(stringArray).run();

        assertEquals(result.size(), 4);
        assertEquals(result.get(0), "A");
        assertEquals(result.get(1), "B");
        assertEquals(result.get(2), "C");
        assertEquals(result.get(3), "D");

      }
    });
  }

}
