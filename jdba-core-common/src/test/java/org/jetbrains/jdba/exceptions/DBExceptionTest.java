package org.jetbrains.jdba.exceptions;

import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@SuppressWarnings("ThrowableInstanceNeverThrown")
public class DBExceptionTest {

  @Test
  public void preserve_DBException() {
    DBException dbe1 = new NoRowsException("test1", null);
    DBException dbe2 = new UnknownDBException("test2", dbe1, null);
    assertThat(dbe2.getCause()).isSameAs(dbe1);
  }

  @Test
  public void preserve_IOException_1_direct() {
    IOException ё = new IOException("test1");
    DBException dbe = new UnknownDBException("test2", ё, null);
    assertThat(dbe.getCause()).isSameAs(ё);
  }

  @Test
  public void preserve_IOException_2_indirect_via_SQLException() {
    IOException ё = new IOException("test1");
    SQLException sqle = new SQLException("test2", ё);
    DBException dbe = new UnknownDBException("test3", sqle, null);
    assertThat(dbe.getCause()).isSameAs(sqle);
    assertThat(dbe.getCause().getCause()).isSameAs(ё);
  }

  @Test
  public void preserve_SQLException_normal_1() {
    SQLException sqle = new SQLTimeoutException("It takes too long time");
    DBException dbe = new UnknownDBException(sqle, null);
    assertThat(dbe.getCause()).isSameAs(sqle);
  }


  @Test
  public void strip_SQLException_1_direct() {
    SQLException monsterException = new MonsterSQLException("reason42", "state42", 42, null);
    DBException dbe = new UnknownDBException(monsterException, null);

    assertThat(dbe.getCause()).isNotInstanceOf(MonsterSQLException.class)
                              .isInstanceOf(StrippedSQLException.class);

    StrippedSQLException cause = (StrippedSQLException) dbe.getCause();
    assertThat(cause.originalClassName).isEqualTo(monsterException.getClass().getName());
    assertThat(cause.getMessage()).contains("reason42");
    assertThat(cause.getSQLState()).isEqualTo("state42");
  }

  @Test
  public void strip_SQLException_1_direct_preserve_IOException_indirect() {
    IOException ё = new IOException("indirect1");
    SQLException monsterException = new MonsterSQLException("reason42", "state42", 42, ё);
    DBException dbe = new UnknownDBException(monsterException, null);

    assertThat(dbe.getCause()).isNotInstanceOf(MonsterSQLException.class)
                              .isInstanceOf(StrippedSQLException.class);

    StrippedSQLException cause = (StrippedSQLException) dbe.getCause();
    assertThat(cause.getCause()).isNotNull()
                                .isSameAs(ё);
  }

  @Test
  public void strip_Throwable_2() {
    Throwable indirect = new UnknownExternalException("indirect2", null);
    Exception direct = new UnknownExternalException("direct1", indirect);
    String message = "ШитHappened";
    DBException dbe = new UnknownDBException(message, direct, null);

    assertThat(dbe.getCause()).isNotInstanceOf(UnknownExternalException.class)
                              .isInstanceOf(StrippedUnknownException.class);

    assertThat(dbe.getMessage()).contains(message);

    StrippedUnknownException cause1 = (StrippedUnknownException) dbe.getCause();
    assertThat(cause1.getMessage()).contains("direct1");
    assertThat(cause1.originalClassName).isEqualTo(UnknownExternalException.class.getName());

    assertThat(cause1.getCause()).isNotInstanceOf(UnknownExternalException.class)
                                 .isInstanceOf(StrippedUnknownException.class);

    StrippedUnknownException cause2 = (StrippedUnknownException) cause1.getCause();
    assertThat(cause2.getMessage()).contains("indirect2");
  }



  private static final class MonsterSQLException extends SQLException {
    private MonsterSQLException(final String reason,
                                final String sqlState,
                                final int vendorCode,
                                final Throwable cause) {
      super(reason, sqlState, vendorCode, cause);
    }
  }


  private static final class UnknownExternalException extends Exception {
    private UnknownExternalException(final String message, final Throwable cause) {
      super(message, cause);
    }
  }



}