package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.DBErrorRecognizer;
import org.jetbrains.jdba.core.DBInterFacade;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcInterFacade implements DBInterFacade {

  @NotNull
  private final DBErrorRecognizer myErrorRecognizer;

  private JdbcInterFacade(@NotNull final DBErrorRecognizer errorRecognizer) {
    myErrorRecognizer = errorRecognizer;
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return UnknownDB.RDBMS;
  }





  @NotNull
  public DBErrorRecognizer getErrorRecognizer() {
    return myErrorRecognizer;
  }
}
