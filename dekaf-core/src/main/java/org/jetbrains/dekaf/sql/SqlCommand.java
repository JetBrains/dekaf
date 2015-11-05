package org.jetbrains.dekaf.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * A SQL command.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SqlCommand extends SqlStatement {

  public SqlCommand(@NotNull final TextFragment sourceFragment) {
    super(sourceFragment);
  }

  public SqlCommand(@NotNull final String sourceText, final int row, @Nullable final String commandName) {
    super(sourceText, row, commandName);
  }


  public SqlCommand(@NotNull final String sourceText) {
    super(sourceText);
  }
}
