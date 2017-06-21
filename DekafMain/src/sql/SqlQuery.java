package org.jetbrains.dekaf.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ResultLayout;

import java.util.function.Function;



/**
 * A SQL query that returns a list of rows.
 *
 * @param <S> type of the query result.
 * @author Leonid Bushuev from JetBrains
 */
public class SqlQuery<S> extends SqlStatement {


  //// STATE \\\\

  @NotNull
  private final ResultLayout<S> myLayout;

  private transient String myDisplayName;


  //// CONSTRUCTORS \\\\


  public SqlQuery(@NotNull final TextFragment sourceFragment,
                  @NotNull final ResultLayout<S> layout) {
    super(sourceFragment);
    this.myLayout = layout;
  }

  public SqlQuery(@NotNull final String sourceText,
                  @NotNull final ResultLayout<S> layout) {
    super(sourceText);
    this.myLayout = layout;
  }

  public SqlQuery(final int row,
                  @NotNull final String sourceText,
                  @Nullable final String name,
                  @NotNull final String description,
                  @NotNull final ResultLayout<S> layout, final String displayName) {
    super(row, sourceText, name, description);
    myLayout = layout;
    this.myDisplayName = displayName;
  }

  protected void setDisplayName(final String displayName) {
    this.myDisplayName = displayName;
  }

  public String getDisplayName() {
    if (myDisplayName == null) {
      prepareDisplayName();
    }
    return myDisplayName;
  }

  private synchronized void prepareDisplayName() {
    if (myDisplayName != null) return;

    int nl = mySourceText.indexOf('\n');
    String str1 = nl > 0 ? mySourceText.substring(0, nl) : mySourceText;
    str1 = str1.trim();

    myDisplayName = str1;
  }


  //// MANIPULATING FUNCTIONS \\\\

  @NotNull
  @Override
  public SqlQuery<S> rewrite(@NotNull final Function<String,String> operator) {
    String transformedSourceText = operator.apply(mySourceText);
    return new SqlQuery<S>(
        myRow,
        transformedSourceText,
        myName,
        myDescription,
        myLayout,
        myDescription
    );
  }



  //// GETTERS AND USEFUL FUNCTIONS \\\\


  @NotNull
  public ResultLayout<S> getLayout() {
    return myLayout;
  }



  //// LEGACY FUNCTIONS \\\\

  @Override
  public String toString() {
    return getDisplayName();
  }
}
