package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;



/**
 * A SQL command.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SqlCommand extends SqlStatement {

  public SqlCommand(@NotNull final TextFragment sourceFragment) {
    super(sourceFragment);
  }

  public SqlCommand(@NotNull final String sourceText, final int row) {
    super(sourceText, row);
  }


  public SqlCommand(@NotNull final String sourceText) {
    super(sourceText);
  }
}
