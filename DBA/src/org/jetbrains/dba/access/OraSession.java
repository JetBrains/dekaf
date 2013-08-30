package org.jetbrains.dba.access;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;



/**
 * Oracle session.
 */
final class OraSession extends BaseSession {


  private static final String STRING_ARRAY_SQL_TYPE = "SYS.TXNAME_ARRAY";


  public OraSession(@NotNull OraFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }


  @Override
  protected boolean assignSpecificParameter(@NotNull PreparedStatement stmt,
                                            int index,
                                            @NotNull Object object) {
    if (object instanceof Collection) {
      Collection<?> collection = (Collection<?>) object;
      String[] javaArray = convertCollectionToStringArray(collection);
      try {
        final OracleConnection oracleConnection = (OracleConnection)stmt.getConnection();
        final OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement) stmt;
        ArrayDescriptor arrayDescriptor =
          ArrayDescriptor.createDescriptor(STRING_ARRAY_SQL_TYPE, oracleConnection);
        ARRAY oracleArray = new ARRAY(arrayDescriptor, oracleConnection, javaArray);
        oraclePreparedStatement.setARRAY(index, oracleArray);
        return true;
      }
      catch (SQLException sqle) {
        throw recognizeError(sqle);
      }
    }

    return false;
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
