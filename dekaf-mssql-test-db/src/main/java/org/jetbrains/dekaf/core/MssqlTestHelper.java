package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.sql.Scriptum;
import org.jetbrains.dekaf.sql.TextFileFragment;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MssqlTestHelper extends BaseTestHelper<DBFacade> {

  public MssqlTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(MssqlTestHelper.class));
    schemasNotToZap.add("sys");
  }


  /*
  @Override
  public void prepareX1() {
    performCommand(scriptum, "X1");
  }
  */

  @Override
  public void prepareX1() {
    final TextFileFragment x1 = scriptum.getText("X1");
    db.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {
        Connection conn =
          session.getSpecificService(Connection.class,
                                     ImplementationAccessibleService.Names.JDBC_CONNECTION);
        assert conn != null;
        try {
          conn.setAutoCommit(true);
          Statement stmt = conn.createStatement();
          stmt.execute(x1.text);
          stmt.close();
        }
        catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }




  @Override
  public void prepareX1000() {
    performCommand(scriptum, "X10");
    performCommand(scriptum, "X1000");
  }

  @Override
  public void prepareX1000000() {
    prepareX1000();
    performCommand(scriptum, "X1000000");
  }


  @Override
  protected void ensureNoTableOrView4(final Object[] params) {
    // Unfortunately, MS SQL provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(scriptum, "EnsureNoForeignKeysMetaQuery", params);
    performMetaQueryCommands(scriptum, "EnsureNoTableOrViewMetaQuery", params);
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    // Unfortunately, MS SQL provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(scriptum, "ZapForeignKeysMetaQuery");
    performMetaQueryCommands(scriptum, "ZapSchemaMetaQuery");
  }
}
