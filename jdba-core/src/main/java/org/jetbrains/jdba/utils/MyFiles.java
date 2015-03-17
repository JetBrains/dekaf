package org.jetbrains.jdba.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.jetbrains.jdba.utils.Strings.matches;



/**
 * File-access utility functions.
 * @author Leonid Bushuev from JetBrains
 * // TODO think out a goode name?
 */
public class MyFiles {

  @NotNull
  public List<File> listFilesRecursively(@NotNull final File dir,
                                         @NotNull final Pattern fileNamePattern) {
    List<File> files = new ArrayList<File>();
    listFilesRecursively(dir, fileNamePattern, files);
    return files;
  }


  private void listFilesRecursively(final @NotNull File dir,
                                    final @NotNull Pattern fileNamePattern,
                                    final @NotNull Collection<File> foundFiles) {
    // handle this level files
    final File[] files = dir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.isFile() && matches(file.getName(), fileNamePattern);
      }
    });
    if (files != null) Collections.addAll(foundFiles, files);

    // handle subdirectories
    final File[] nestedDirs = dir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.isDirectory();
      }
    });
    if (nestedDirs != null) {
      for (File nestedDir : nestedDirs) {
        listFilesRecursively(nestedDir, fileNamePattern, foundFiles);
      }
    }
  }
}
