package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;



/**
 * A SQL query that returns a list of rows.
 *
 * @param <S> type of the query result.
 * @author Leonid Bushuev from JetBrains
 */
public class Query<S> extends Executable {
  @NotNull
  final String sourceText;

  @NotNull
  final RowsCollector<S> collector;


  private transient String displayName;


  public Query(@NotNull final String sourceText,
               @NotNull final RowsCollector<S> collector) {
    this.sourceText = sourceText;
    this.collector = collector;
  }


  public String getDisplayName() {
    if (displayName == null) {
      prepareDisplayName();
    }
    return displayName;
  }


  private synchronized void prepareDisplayName() {
    if (displayName != null) return;

    int nl = sourceText.indexOf('\n');
    String str1 = nl > 0 ? sourceText.substring(0, nl) : sourceText;
    str1 = str1.trim();

    displayName = str1;
  }


  @Override
  public String toString() {
    return getDisplayName();
  }
}
