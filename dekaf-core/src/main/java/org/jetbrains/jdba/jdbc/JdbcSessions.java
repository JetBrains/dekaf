package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.DBLeasedSession;
import org.jetbrains.jdba.core.DBSessions;
import org.jetbrains.jdba.exceptions.DBFactoryException;
import org.jetbrains.jdba.intermediate.IntegralIntermediateRdbmsProvider;

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
    return wrap(intermediateSession);
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
    return wrap(intermediateSession);
  }



}
