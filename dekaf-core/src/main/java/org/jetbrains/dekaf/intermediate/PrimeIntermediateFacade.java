package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.core.ImplementationAccessibleService;



/**
 * Portable intermediate DB facade.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface PrimeIntermediateFacade extends ImplementationAccessibleService {


  //// DATABASE PROPERTIES FUNCTIONS \\\\

  /**
   * The DBMS this facade is applicable/connected to.
   */
  @NotNull
  Rdbms rdbms();


  //// CONNECT/DISCONNECT METHODS \\\\

  /**
   * Specify how many connection to hold at most.
   * @param sessionsLimit the maximum number of connections.
   */
  //void setSessionsLimit(int sessionsLimit);

  /**
   * Connects to the database server.
   */
  void connect();

  /**
   * Reconnects from the database server.
   */
  void reconnect();

  /**
   * Disconnects from the database server.
   *
   * <p>
   *   If not connected - does nothing.
   * </p>
   */
  void disconnect();

  /**
   * Checks whether it is connected to the server.
   *
   * <p>
   *   TBD does it really performs a "ping" interaction or just returning a kind of internal status?
   * </p>
   *
   * @return whether is connected.
   */
  boolean isConnected();

  /**
   * Provides a brief connection info.
   * @return a brief connection info.
   * @throws IllegalStateException the facade is not connected.
   */
  ConnectionInfo getConnectionInfo();


  //// SESSIONS CONTROL \\\\

  @NotNull
  PrimeIntermediateSession openSession();



}
