package org.jetbrains.dekaf.jdbc;

import org.hamcrest.core.IsEqual;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.TestEnvironment;
import org.jetbrains.dekaf.core.*;
import org.jetbrains.dekaf.exceptions.DBColumnAccessDeniedException;
import org.jetbrains.dekaf.exceptions.DBLoginFailedException;
import org.jetbrains.dekaf.sql.SqlQuery;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.core.Layouts.existence;
import static org.junit.Assume.assumeThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SybaseExceptionRecognizerTest extends CommonExceptionRecognizingTest {

  @Test(expected = DBColumnAccessDeniedException.class)
  public void recognize_PermissionDeniedOnColumns() {
    final String queryText = "select * from master.dbo.sysdatabases";
    final SqlQuery<Boolean> query = new SqlQuery<Boolean>(queryText, existence());

    DB.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        tran.query(query).run();

      }
    });
  }


  @Test(expected = DBLoginFailedException.class)
  public void recognize_LoginPasswordIncorrect() {
    Pattern passwordPattern = Pattern.compile("password=(.*)(;|$)");
    String connectionString = TestEnvironment.obtainConnectionString();
    Matcher m = passwordPattern.matcher(connectionString);
    boolean ok = m.find();
    assumeThat(ok, IsEqual.equalTo(true));

    connectionString = connectionString.substring(0, m.start(1)) + "labuda" + connectionString.substring(m.end(1));

    BaseFederatedProvider provider = new BaseFederatedProvider();
    provider.initLocally();

    DBFacade facade = provider.openFacade(connectionString, null, 1, false);
    assertThat(facade).isNotNull();
    facade.connect();
  }


}
