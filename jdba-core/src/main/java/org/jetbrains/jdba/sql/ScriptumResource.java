package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

import static org.jetbrains.jdba.util.Collects.collectionToString;



/**
 * Represents one resource of SQL texts.
 *
 * @author Leonid Bushuev from JetBrains
 */
abstract class ScriptumResource {


  //// STATE \\\\

  /**
   * All fragments.
   *
   * <p>
   *   Null means not loaded yet.
   * </p>
   */
  protected TextFileFragment[] myFragments = null;

  /**
   * Map of name to fragment.
   *
   * <p>
   *   Null means not loaded yet.
   * </p>
   */
  protected Map<String,TextFileFragment> myFragmentsMap = null;



  //// INITIALIZATION \\\\

  @NotNull
  protected abstract TextFileFragment[] loadFragments();


  protected void loadIfNeeded() {
    if (myFragments == null) {
      myFragments = loadFragments();

      myFragmentsMap = new TreeMap<String, TextFileFragment>(String.CASE_INSENSITIVE_ORDER);
      for (TextFileFragment fragment : myFragments) {
        if (fragment.getFragmentName() != null) {
          myFragmentsMap.put(fragment.getFragmentName(), fragment);
        }
      }
    }
  }


  //// IMPLEMENTATION \\\\


  @NotNull
  TextFileFragment get(@NotNull final String name) {
    TextFileFragment fragment = find(name);

    if (fragment == null) {
      if (myFragmentsMap == null) {
        throw new IllegalStateException("The scriptum resource is not loaded yet.");
      }
      else {
        String msg = "No such fragment with name: " + name + '\n' +
                     collectionToString(myFragmentsMap.keySet(),
                                        ", ",
                                        "There are fragments: ",
                                        ".",
                                        "This resource is empty");
        throw new IllegalArgumentException(msg);
      }
    }

    return fragment;
  }


  @Nullable
  TextFileFragment find(@NotNull final String name) {
    loadIfNeeded();
    return myFragmentsMap.get(name);
  }


  @NotNull
  String[] getExistentNames() {
    loadIfNeeded();
    int n = myFragments.length;
    String[] names = new String[n];
    for (int i = 0; i < n; i++) names[i] = myFragments[i].getFragmentName();
    return names;
  }


  int count() {
    loadIfNeeded();
    return myFragments.length;
  }

}
