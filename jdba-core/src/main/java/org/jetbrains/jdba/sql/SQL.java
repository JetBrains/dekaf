package org.jetbrains.jdba.sql;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core1.DBRowsCollector;
import org.jetbrains.jdba.util.JavaResource;
import org.jetbrains.jdba.util.Resource;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * SQL queries and commands factory.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SQL implements Serializable, Cloneable {


  @NotNull
  private final CopyOnWriteArrayList<Resource> myResources = new CopyOnWriteArrayList<Resource>();


  public void assignResources(@Nullable final ClassLoader classLoader, @NotNull final String path) {
    ClassLoader cl = classLoader == null ? this.getClass().getClassLoader() : classLoader;
    assert cl != null : "Failed to obtain an instance of ClassLoader";
    JavaResource jrc = new JavaResource(cl, path);
    myResources.add(jrc);
  }


  public void assignResources(@NotNull final Class clazz) {
    assignResources(clazz, null);
  }

  public void assignResources(@NotNull final Class clazz, @Nullable final String relativePath) {
    ClassLoader classLoader = clazz.getClassLoader();
    assert classLoader != null;
    Package pakkage = clazz.getPackage();
    assert pakkage != null;
    String path = pakkage.getName().replace('.', '/');
    if (relativePath != null) {
      path += '/' + relativePath;
    }
    assignResources(classLoader, path);
  }


  @NotNull
  String getSourceText(@NotNull final String name) {
    int colon = name.indexOf(':');
    if (colon > 0) {
      String primaryName = name.substring(0, colon).trim();
      String innerName = name.substring(colon+1).trim();
      String fullText = getSourceText(primaryName);
      return extractNamedSubtext(fullText, primaryName, innerName);
    }
    else {
      final String nameWithExtension = name.endsWith(".sql") ? name : name + ".sql";
      final String textFromResources = getTextFromResources(nameWithExtension);
      if (textFromResources == null) {
        throw new IllegalArgumentException("Resource '"+nameWithExtension+"' not found.");
      }
      return textFromResources;
    }
  }

  private static final Pattern NAMED_TEXT_PATTERN =
    Pattern.compile("\\s*(--=--)\\s*(\\w+)\\s*\\n+(.*?)(\\n\\s*/\\s*)?(\\n+\\s*(--=--)|$)",
                    Pattern.DOTALL);


  static String extractNamedSubtext(@NotNull final String fullText,
                                    @NotNull final String primaryName,
                                    @NotNull final String innerName) {
    Matcher m = NAMED_TEXT_PATTERN.matcher(fullText);
    boolean found = m.find();
    while (found) {
      String sectionName = m.group(2);
      if (sectionName.equalsIgnoreCase(innerName)) {
        String sectionText = m.group(3).trim();
        return sectionText;
      }
      int nextPosition = m.start(5);
      if (nextPosition > 0) found = m.find(nextPosition);
      else found = false;
    }
    throw new IllegalArgumentException("Resource '"+primaryName+"' doesn't contain section named '"+innerName+"'.");
  }


  @Nullable
  private String getTextFromResources(@NotNull final String name) {
    for (Resource resource : myResources) {
      // TODO make it more robust: continue to find the text in other resources
      //      and throw of not found, with all problems in the message
      try {
        final String text = resource.loadText(name);
        if (text != null) return text;
      }
      catch (IOException ioe) {
        throw new IllegalArgumentException("Could not access resource: " + resource);
      }
    }
    return null;
  }



  @NotNull
  public SQLCommand command(@NotNull final String sourceText) {
    String text = preprocessOneStatementText(sourceText);
    return new SQLCommand(text);
  }


  @NotNull
  public <S> SQLQuery<S> query(@NotNull final String sourceText,
                               @NotNull final DBRowsCollector<S> collector) {
    String text = preprocessOneStatementText(sourceText);
    return new SQLQuery<S>(text, collector);
  }


  private String preprocessOneStatementText(@NotNull final String sourceText) {
    String text = sourceText.trim();

    // check whether it is a source name
    text = substituteReferredText(text);

    // drop the tailing semicolon
    //text = Strings.removeEnding(text, ";").trim();

    // ok
    return text;
  }


  private String substituteReferredText(@NotNull final String text) {
    if (text.startsWith("##")) {
      String sourceName = text.substring(2).trim();
      final String newText = getSourceText(sourceName).trim();
      return newText;
      //return substituteReferredText(newText);
    }
    else {
      return text;
    }
  }


  /**
   * Creates a SQL script class from the given text(s) with scripts.
   * @param sourceText  one or several texts,
   *                    every text may contain one or more SQL statements.
   * @return SQL script.
   */
  @NotNull
  public SQLScript script(@NotNull final String sourceText) {
    final SQLScriptBuilder scriptBuilder = scriptBuilder();
    String text = substituteReferredText(sourceText);
    scriptBuilder.parse(text);
    return scriptBuilder.build();
  }


  @NotNull
  public SQLScriptBuilder scriptBuilder() {
    return new SQLScriptBuilder();
  }

  @NotNull
  protected SQLScript instantiateSQLScript(@NotNull final ImmutableList<SQLCommand> commands) {
    return new SQLScript(commands);
  }


  @NotNull
  protected SQLCommand instantiateCommand(final int begin, @NotNull final String sourceText) {
    return new SQLCommand(begin, sourceText);
  }


  @NotNull
  protected SQLScript getEmptyScript() {
    return EMPTY_SCRIPT;
  }

  static final SQLScript EMPTY_SCRIPT = new SQLScript(ImmutableList.<SQLCommand>of());



  @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
  @Override
  public SQL clone() {
    try {
      SQL clone = (SQL) super.clone();
      clone.myResources.addAll(this.myResources);
      return clone;
    }
    catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

}
