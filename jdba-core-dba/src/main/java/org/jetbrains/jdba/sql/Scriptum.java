package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.ResultLayout;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jetbrains.jdba.util.Strings.eq;
import static org.jetbrains.jdba.util.Strings.rtrim;



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
    if (eq(parentScriptum.myDialect, dialect)) {
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
  public final TextFragment findText(@NotNull String name) {
    TextFragment fragment;
    final String nameWithDialect = myDialect == null ? null : name + '+' + myDialect;
    for (int i = myResources.length-1; i >= 0; i--) {
      ScriptumResource r = myResources[i];
      if (nameWithDialect != null) {
        fragment = r.find(nameWithDialect);
        if (fragment != null) return fragment;
      }
      fragment = r.find(name);
      if (fragment != null) return fragment;
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
  public final TextFragment getText(@NotNull String name) throws ScriptNotFoundException{
    TextFragment fragment = findText(name);
    if (fragment == null) throw new ScriptNotFoundException("No such script: " + name);
    return fragment;
  }


  @NotNull
  public final <S> SqlQuery<S> query(@NotNull final String name,
                                     @NotNull final ResultLayout<S> layout) {
    TextFragment fragment = getText(name);
    fragment = stripSingleStatement(fragment);
    return new SqlQuery<S>(fragment, layout);
  }

  @NotNull
  public final SqlCommand command(@NotNull final String name) {
    TextFragment fragment = getText(name);
    fragment = stripSingleStatement(fragment);
    return new SqlCommand(fragment);
  }

  @NotNull
  public final SqlScript script(@NotNull final String name) {
    TextFragment fragment = getText(name);
    SqlScriptBuilder b = new SqlScriptBuilder();
    b.parse(fragment.text);
    return new SqlScript(b.build());
  }


  private static final Pattern STRIP_SINGLE_STATEMENT_PATTERN =
      Pattern.compile("((\\;\\s*)+|(\\n\\/\\s*\\n?)+)$", Pattern.DOTALL);

  @NotNull
  private TextFragment stripSingleStatement(@NotNull final TextFragment fragment) {
    Matcher m = STRIP_SINGLE_STATEMENT_PATTERN.matcher(fragment.text);
    if (m.find()) {
      int n = fragment.text.length();
      n -= m.group(1).length();
      return new TextFragment(rtrim(fragment.text.substring(0, n)), fragment.row, fragment.pos);
    }
    else {
      return fragment;
    }
  }



  //// EXCEPTIONS \\\\

  public static class ScriptNotFoundException extends RuntimeException {
    private ScriptNotFoundException(final String message) {
      super(message);
    }
  }

}
