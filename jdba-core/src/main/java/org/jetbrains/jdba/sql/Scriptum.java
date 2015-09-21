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

  @NotNull
  public static Scriptum of(@NotNull final Class clazz) {
    ClassLoader classLoader = clazz.getClassLoader();
    String className = clazz.getName();
    String path = className.replace('.', '/');
    String resourceName = path + ".sql";

    return of(classLoader, resourceName);
  }

  @NotNull
  public static Scriptum of(@NotNull final Class clazz, @NotNull final String name) {
    ClassLoader classLoader = clazz.getClassLoader();
    String packageName = clazz.getPackage().getName();
    String path = packageName.replace('.', '/');
    String fileName = name;
    if (!fileName.contains(".")) fileName += ".sql";
    String resourceName = path + '/' + fileName;

    return of(classLoader, resourceName);
  }

  @NotNull
  public static Scriptum dialectOf(@NotNull final Class clazz, @Nullable final String dialect) {
    ClassLoader classLoader = clazz.getClassLoader();
    String className = clazz.getName();
    String path = className.replace('.', '/');
    ArrayList<ScriptumResource> sr = new ArrayList<ScriptumResource>(2);

    if (dialect != null) {
      String name1 = path + '+' + dialect + ".sql";
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
                                                       className));

    ScriptumResource[] resources = sr.toArray(new ScriptumResource[sr.size()]);
    return new Scriptum(resources, dialect);
  }

  @NotNull
  public static Scriptum dialectOf(@NotNull final Scriptum parentScriptum,
                                   @Nullable final String dialect) {
    if (eq(parentScriptum.myDialect, dialect)) {
      return parentScriptum;
    }
    else {
      return new Scriptum(parentScriptum.myResources, dialect);
    }
  }


  @NotNull
  public static Scriptum of(final ClassLoader classLoader, final String resourceName) {
    ArrayList<ScriptumResource> sr = new ArrayList<ScriptumResource>(2);
    boolean exists = classLoader.getResource(resourceName) != null;
    if (exists) {
      ScriptumResourceFromJava r2 = new ScriptumResourceFromJava(classLoader, resourceName);
      sr.add(r2);
    }
    else {
      throw new IllegalArgumentException(String.format("Resources for class %s not found",
                                                       resourceName));
    }

    ScriptumResource[] resources = sr.toArray(new ScriptumResource[sr.size()]);
    return new Scriptum(resources, null);
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
  public final TextFileFragment findText(@NotNull String name) {
    TextFileFragment fragment;
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
  public final TextFileFragment getText(@NotNull String name) throws ScriptNotFoundException{
    TextFileFragment fragment = findText(name);
    if (fragment != null) {
      return fragment;
    }
    else {
      // some useful diagnostics
      StringBuilder b = new StringBuilder();
      b.append("No such script with name: ").append(name).append('\n');
      boolean was = false;
      for (ScriptumResource r : myResources) {
        for (String existentName : r.getExistentNames()) {
          if (!was) {
            b.append("There are scripts: ");
            was = true;
          }
          else {
            b.append(", ");
          }
          b.append(existentName);
        }
      }
      if (!was) {
        b.append("There are no scripts at all");
      }
      throw new ScriptNotFoundException(b.toString());
    }
  }


  @NotNull
  public final <S> SqlQuery<S> query(@NotNull final String name,
                                     @NotNull final ResultLayout<S> layout) {
    TextFileFragment fragment = getText(name);
    fragment = stripSingleStatement(fragment);
    final SqlQuery<S> query = new SqlQuery<S>(fragment, layout);
    query.setDisplayName(fragment.getFragmentName());
    return query;
  }

  @NotNull
  public final SqlCommand command(@NotNull final String name) {
    TextFileFragment fragment = getText(name);
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
  private TextFileFragment stripSingleStatement(@NotNull final TextFileFragment fragment) {
    Matcher m = STRIP_SINGLE_STATEMENT_PATTERN.matcher(fragment.text);
    if (m.find()) {
      int n = fragment.text.length();
      n -= m.group(1).length();
      final String text = rtrim(fragment.text.substring(0, n));
      return new TextFileFragment(text,
                                  fragment.getTextName(),
                                  fragment.row, fragment.pos,
                                  fragment.getFragmentName());
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
