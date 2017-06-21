package org.jetbrains.dekaf.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;



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


  private SqlCommand(final int row,
                     @NotNull final String sourceText,
                     @Nullable final String name,
                     @NotNull final String description) {
    super(row, sourceText, name, description);
  }


  @NotNull
  @Override
  public SqlCommand rewrite(@NotNull final Function<String,String> operator) {
    String transformedSourceTex = operator.apply(mySourceText);
    return new SqlCommand(myRow, transformedSourceTex, myName, myDescription);
  }

}
