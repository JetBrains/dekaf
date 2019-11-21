package org.jetbrains.dekaf.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class TextFileFragment extends TextFragment {

  @NotNull
  private final String myFileName;

  @Nullable
  private final String myFragmentName;


  /**
   * Instantiates a new TextFragment.
   * @param text          text fragment itself.
   * @param fileName      the name of file this fragment from.
   * @param row           the row number, starts with 1.
   * @param pos           the number of the position inside the row, starts with 1.
   * @param fragmentName  optional name of the fragment.
   */
  public TextFileFragment(@NotNull final String text,
                          @NotNull final String fileName,
                          final int row,
                          final int pos,
                          @Nullable final String fragmentName) {
    super(text, row, pos);
    myFileName = fileName;
    myFragmentName = fragmentName;
  }


  @NotNull
  @Override
  protected String getTextName() {
    return myFileName;
  }

  @Nullable
  public String getFragmentName() {
    return myFragmentName;
  }

  @NotNull
  @Override
  protected String getTextLocationHumanReadable() {
    String location = "Fragment at " + myFileName + ':' + row + ':' + pos;
    if (myFragmentName != null) location += " (" + myFragmentName + ")";
    return location;
  }

}
