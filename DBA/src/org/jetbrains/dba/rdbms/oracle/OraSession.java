package org.jetbrains.dba.rdbms.oracle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.core.BaseCommandRunner;
import org.jetbrains.dba.core.BaseSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;



/**
 * Oracle session.
 */
final class OraSession extends BaseSession {


  public OraSession(@NotNull OraFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }


  @Override
  protected BaseCommandRunner command(@NotNull String commandText, int offset) {
    return new OraCommandRunner(this, commandText, offset);
  }


  @Override
  protected boolean assignSpecificParameter(@NotNull PreparedStatement stmt,
                                            int index,
                                            @NotNull Object object) throws SQLException {
    if (object instanceof Collection) {
      Collection<?> collection = (Collection<?>) object;
      String[] javaArray = convertCollectionToStringArray(collection);

      OracleSpecificStuff.assignOracleArray(stmt, index, javaArray);
      return true;
    }
    else {
      return false;
    }
  }


  private static String[] convertCollectionToStringArray(Collection<?> collection) {
    int i = 0, n = collection.size();
    final String[] array = new String[n];
    for (Object item : collection) {
      array[i++] = item.toString();
    }
    return array;
  }
}
