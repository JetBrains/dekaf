package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;



/**
 * A SQL command.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SqlCommand extends SqlExecutable {


  public SqlCommand(final int lineOffset, @NotNull final String sourceText) {
    super(sourceText, lineOffset);
  }


  public SqlCommand(@NotNull final String sourceText) {
    this(0, sourceText);
  }
}
