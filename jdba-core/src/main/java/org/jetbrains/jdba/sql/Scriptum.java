package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.DBRowsCollector;
import org.jetbrains.jdba.utils.Strings;

import java.util.ArrayList;



/**
 * Scriptum provides with access to SQL texts that are placed in separate files
 * (not within Java code).
 *
 * @author Leonid Bushuev from JetBrains
 */
public final class Scriptum {

  @NotNull
  private final ScriptumResource[] myResources;

  @Nullable
  private final String myDialect;



  //// CONSTRUCTORS \\\\

  public static Scriptum of(@NotNull final Class clazz) {
    return of(clazz, null);
  }

  public static Scriptum of(@NotNull final Class clazz, @Nullable final String dialect) {
    ClassLoader classLoader = clazz.getClassLoader();
    String path = clazz.getName().replace('.', '/');
    ArrayList<ScriptumResource> sr = new ArrayList<ScriptumResource>(2);

    if (dialect != null) {
      String name1 = path + '.' + dialect + ".sql";
      boolean exist1 = classLoader.getResource(name1) != null;
      if (exist1) {
        ScriptumResourceFromJava r1 = new ScriptumResourceFromJava(classLoader, name1);
        sr.add(r1);
      }
    }

    String name2 = path + ".sql";
    boolean exist2 = classLoader.getResource(name2) != null;
    if (exist2) {
      ScriptumResourceFromJava r2 = new ScriptumResourceFromJava(classLoader, name2);
      sr.add(r2);
    }

    if (sr.isEmpty())
      throw new IllegalArgumentException(String.format("Resources for class %s not found",
                                                       clazz.getName()));

    ScriptumResource[] resources = sr.toArray(new ScriptumResource[sr.size()]);
    return new Scriptum(resources, dialect);
  }

  public static Scriptum of(@NotNull final Scriptum parentScriptum, @Nullable final String dialect) {
    if (Strings.eq(parentScriptum.myDialect, dialect)) {
      return parentScriptum;
    }
    else {
      return new Scriptum(parentScriptum.myResources, dialect);
    }
  }



  private Scriptum(@NotNull final ScriptumResource[] resources,
                   @Nullable final String dialect) {
    myResources = resources;
    myDialect = dialect;
  }


  //// FUNCTIONS TO QUERY TEXTS \\\\

  /**
   * Finds the requested script.
   *
   * <p>
   *   If no such script, returns null.
   * </p>
   *
   * @param name  name of the script to find.
   * @return      the found script, or <i>null</i> if not found.
   */
  @Nullable
  public final String findText(@NotNull String name) {
    String text;
    final String nameWithDialect = myDialect == null ? null : name + '+' + myDialect;
    for (int i = myResources.length-1; i >= 0; i--) {
      ScriptumResource r = myResources[i];
      if (nameWithDialect != null) {
        text = r.find(nameWithDialect);
        if (text != null) return text;
      }
      text = r.find(name);
      if (text != null) return text;
    }
    return null;
  }

  /**
   * Provides with the requested script.
   *
   * <p>
   *   If no such script, throws {@link ScriptNotFoundException}.
   * </p>
   *
   * @param name  name of the script to provide with.
   * @return      the script.
   * @throws ScriptNotFoundException if no such script.
   */
  @NotNull
  public final String getText(@NotNull String name) throws ScriptNotFoundException{
    String text = findText(name);
    if (text == null) throw new ScriptNotFoundException("No such script: " + name);
    return text;
  }


  @NotNull
  public final <S> SQLQuery<S> query(@NotNull final String name,
                                     @NotNull final DBRowsCollector<S> collector) {
    String text = getText(name);
    return new SQLQuery<S>(text, collector);
  }

  @NotNull
  public final SQLCommand command(@NotNull final String name) {
    String text = getText(name);
    return new SQLCommand(text);
  }

  @NotNull
  public final SQLScript script(@NotNull final String name) {
    String text = getText(name);
    SQLScriptBuilder b = new SQLScriptBuilder();
    b.parse(text);
    return new SQLScript(b.build());
  }


  //// EXCEPTIONS \\\\

  public static class ScriptNotFoundException extends RuntimeException {
    private ScriptNotFoundException(final String message) {
      super(message);
    }
  }

}
