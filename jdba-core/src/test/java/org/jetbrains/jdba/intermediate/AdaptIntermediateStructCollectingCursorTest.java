package org.jetbrains.jdba.intermediate;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.*;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.listOf;
import static org.jetbrains.jdba.core.Layouts.structOf;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class AdaptIntermediateStructCollectingCursorTest extends BaseHyperSonicFacadeTest {

  public static class PrimitiveNumbers {
    byte B;
    short S;
    int I;
    long L;
  }


  @Test
  public void empty_cursor() {
    final SqlQuery<List<PrimitiveNumbers>> query =
        new SqlQuery<List<PrimitiveNumbers>>(
            "select 11,22,33,44 from information_schema.schemata where 1 is null",
            listOf(structOf(PrimitiveNumbers.class)));
    AdaptIntermediateFacade adaptedFacade =
        new AdaptIntermediateFacade(myJdbcFacade);

    DBFacade theFacade = new BaseFacade(adaptedFacade);
    theFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {
        List<PrimitiveNumbers> result = session.query(query).run();
        assertThat(result).isEmpty();
      }
    });
  }

}