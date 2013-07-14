package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Registry for database facades.
 *
 * @see DBFacade
 */
public final class DatabaseRegistry {

  private static final Map<String, DBFacade> DBMS =
    new ConcurrentHashMap<String, DBFacade>(5);


  public static void registerDBMS(final @NotNull DBFacade dbFacade) {
    String dbmsCode = dbFacade.getDbmsCode();
    DBMS.put(dbmsCode, dbFacade);
  }
}
