package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;



/**
 * A SQL command.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SQLCommand extends SQLExecutable {


  public SQLCommand(final int lineOffset, @NotNull final String sourceText) {
    super(sourceText, lineOffset);
  }


  public SQLCommand(@NotNull final String sourceText) {
    this(0, sourceText);
  }
}
