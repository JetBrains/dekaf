package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.jetbrains.jdba.util.Collects.collectionToString;



/**
 * Represents one resource of SQL texts.
 *
 * @author Leonid Bushuev from JetBrains
 */
abstract class ScriptumResource {

  @Nullable
  protected Map<String,TextFileFragment> myScripts = null;


  protected abstract void load();


  @NotNull
  TextFileFragment get(@NotNull final String name) {
    TextFileFragment fragment = find(name);

    if (fragment == null) {
      if (myScripts == null) {
        throw new IllegalStateException("The scriptum resource is not loaded yet.");
      }
      else {
        String msg = "No such fragment with name: " + name + '\n' +
                     collectionToString(myScripts.keySet(), ", ", "There are fragments: ", ".", "This resource is empty");
        throw new IllegalArgumentException(msg);
      }
    }

    return fragment;
  }


  @Nullable
  TextFileFragment find(@NotNull final String name) {
    if (myScripts == null) {
      load();
      assert myScripts != null;
    }

    return myScripts.get(name);
  }


  @NotNull
  Set<String> getExistentNames() {
    return myScripts != null
             ? Collections.unmodifiableSet(myScripts.keySet())
             : Collections.<String>emptySet();
  }


  int count() {
    if (myScripts == null) {
      load();
      assert myScripts != null;
    }

    return myScripts.size();
  }

}
