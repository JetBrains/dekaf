package org.jetbrains.jdba.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface Resource {

  @Nullable
  String loadText(@NotNull String name) throws IOException;

}
