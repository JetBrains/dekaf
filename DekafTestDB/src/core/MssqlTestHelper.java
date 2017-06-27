package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.text.Scriptum;
import org.jetbrains.dekaf.text.TextFileFragment;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MssqlTestHelper extends BaseTestHelper<DBFacade> {

  public MssqlTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(MssqlTestHelper.class));
    getSchemasNotToZap().add("sys");
  }


  /*
  @Override
  public void prepareX1() {
    performCommand(scriptum, "X1");
  }
  */

  @Override
  public void prepareX1() {
    final TextFileFragment x1 = getScriptum().getText("X1");
    getDb().inSessionDo(session -> {
        Connection conn =
          session.getSpecificService(Connection.class,
                                     ImplementationAccessibleService.Names.JDBC_CONNECTION);
        assert conn != null;
        try {
          conn.setAutoCommit(true);
          Statement stmt = conn.createStatement();
          stmt.execute(x1.getText());
          stmt.close();
        }
        catch (SQLException e) {
          throw new RuntimeException(e);
        }
    });
  }




  @Override
  public void prepareX1000() {
    performCommand(getScriptum(), "X10");
    performCommand(getScriptum(), "X1000");
  }

  @Override
  public void prepareX1000000() {
    prepareX1000();
    performCommand(getScriptum(), "X1000000");
  }


}
