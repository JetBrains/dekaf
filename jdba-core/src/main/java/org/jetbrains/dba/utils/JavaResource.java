package org.jetbrains.dba.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

import static org.jetbrains.dba.utils.Strings.ensureEndsWith;
import static org.jetbrains.dba.utils.Strings.ensureStartsWith;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JavaResource implements Resource {

  @NotNull
  private final ClassLoader myClassLoader;

  /**
   * The path prefix,
   * contains the first and last slashes.
   */
  @NotNull
  private final String myPath;


  public JavaResource(@NotNull final ClassLoader classLoader, @NotNull final String path) {
    myClassLoader = classLoader;
    myPath = ensureEndsWith(path, '/');
  }


  @Nullable
  @Override
  public String loadText(@NotNull String name) throws IOException {
    String fullName = myPath + name;
    final URL resourceUrl = myClassLoader.getResource(fullName);
    if (resourceUrl == null) return null;
    return Resources.toString(resourceUrl, Charsets.UTF_8);
  }


  @Override
  public String toString() {
    return "JavaResource("+myClassLoader+':'+myPath+')';
  }
}
