package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.exceptions.DBSessionIsClosedException;
import org.jetbrains.dekaf.intermediate.AdaptIntermediateSession;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateSession;
import org.jetbrains.dekaf.intermediate.PrimeIntermediateSession;



/**
 * An utility class designed for working with DB sessions.
 *
 * @see DBSessions
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class DBSessions {

  /**
   * Wraps the given integral intermediate session with a new instance of DBSession.
   * @param intermediateSession  an intermediate session to wrap.
   * @return  new instance of DBSession.
   */
  @NotNull
  public static DBLeasedSession wrap(@NotNull final IntegralIntermediateSession intermediateSession) {
    if (intermediateSession.isClosed()) throw new DBSessionIsClosedException("The session is closed.");
    return new BaseSession(intermediateSession);
  }


  /**
   * Wraps the given prime intermediate session with a new instance of DBSession.
   * @param intermediateSession  an intermediate session to wrap.
   * @return  new instance of DBSession.
   */
  @NotNull
  public static DBLeasedSession wrap(@NotNull final PrimeIntermediateSession intermediateSession) {
    if (intermediateSession.isClosed()) throw new DBSessionIsClosedException("The session is closed.");

    if (intermediateSession instanceof IntegralIntermediateSession) {
      return wrap((IntegralIntermediateSession)intermediateSession);
    }
    else {
      AdaptIntermediateSession adaptedSession =
          new AdaptIntermediateSession(intermediateSession);
      return wrap(adaptedSession);
    }
  }


}
