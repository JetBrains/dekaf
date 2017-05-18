package org.jetbrains.dekaf.core;



import org.jetbrains.dekaf.sql.Rewriters;
import org.jetbrains.dekaf.sql.SqlQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.core.Layouts.rowOf;
import static org.jetbrains.dekaf.core.Layouts.structOf;



public class RedshiftQueryRunnerTest extends CommonQueryRunnerTest {

  @Override
  public void query_calendar_values_now() {
    String queryText = "select NOW::timestamp as javaDate, " +
        "NOW::timestamp as sqlDate, " +
        "NOW::timestamp as sqlTimestamp, " +
        "NOW::timestamp as sqlTime";
    SqlQuery<CalendarValues> query = new SqlQuery<CalendarValues>(
        queryText, rowOf(structOf(CalendarValues.class))
    ).rewrite(Rewriters.replace("NOW", sqlNow()));

    CalendarValues cv = query(query);

    assertThat(cv.javaDate)    .isExactlyInstanceOf(java.util.Date.class);
    assertThat(cv.sqlDate)     .isExactlyInstanceOf(java.sql.Date.class);
    assertThat(cv.sqlTimestamp).isExactlyInstanceOf(java.sql.Timestamp.class);
    assertThat(cv.sqlTime)     .isExactlyInstanceOf(java.sql.Time.class);
  }

}
