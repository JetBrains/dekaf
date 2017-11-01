package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.DBLeasedSession;
import org.jetbrains.dekaf.core.DBSessions;
import org.jetbrains.dekaf.exceptions.DBFactoryException;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateRdbmsProvider;

import java.sql.Connection;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcSessions extends DBSessions {

  @NotNull
  public static DBLeasedSession wrap(@NotNull final Connection connection,
                                     @NotNull final JdbcIntermediateRdbmsProvider rdbmsProvider,
                                     final boolean takeOwnership) {
    final JdbcIntermediateSession intermediateSession =
        rdbmsProvider.wrapConnection(connection, takeOwnership);
    return DBSessions.wrap(intermediateSession);
  }


  @NotNull
  public static DBLeasedSession wrap(@NotNull final Connection connection,
                                     @NotNull final JdbcIntermediateFederatedProvider federatedProvider,
                                     @NotNull final Rdbms rdbms,
                                     final boolean takeOwnership) {
    final IntegralIntermediateRdbmsProvider rdbmsProvider =
        federatedProvider.getSpecificServiceProvider(rdbms);
    if (rdbmsProvider == null) {
      throw new DBFactoryException("RDBMS provider for " + rdbms.code + " is not registered.");
    }
    if (!(rdbmsProvider instanceof JdbcIntermediateRdbmsProvider)) {
      throw new DBFactoryException("Looks like the obtained provider for " + rdbms.code + " doesn't support JDBC. " +
                                   "It's class: " + rdbmsProvider.getClass().getName() + ".");
    }

    final JdbcIntermediateSession intermediateSession =
        ((JdbcIntermediateRdbmsProvider)rdbmsProvider).wrapConnection(connection, takeOwnership);
    return DBSessions.wrap(intermediateSession);
  }



}
