package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;



/**
 * Represents one resource of SQL texts.
 *
 * @author Leonid Bushuev from JetBrains
 */
abstract class ScriptumResource {

  @Nullable
  protected Map<String,TextFragment> myScripts = null;


  protected abstract void load();


  TextFragment find(@NotNull final String name) {
    if (myScripts == null) {
      load();
      assert myScripts != null;
    }

    return myScripts.get(name.toUpperCase(Locale.GERMAN));
  }


  int count() {
    if (myScripts == null) {
      load();
      assert myScripts != null;
    }

    return myScripts.size();
  }

}
