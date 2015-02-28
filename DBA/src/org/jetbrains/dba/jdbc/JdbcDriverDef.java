package org.jetbrains.dba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;

import java.util.regex.Pattern;



/**
 * Defines a JDBC driver.
 *
 * <p>Value object.</p>
 *
 * @author Leonid Bushuev from JetBrains
 */
final class JdbcDriverDef {

  @NotNull public final Rdbms rdbms;
  @NotNull public final Pattern connectionStringPattern;
  @NotNull public final Pattern usualJarNamePattern;
  @NotNull public final String driverClassName;


  JdbcDriverDef(@NotNull Rdbms rdbms,
                @NotNull String connectionStringPattern,
                @NotNull String usualJarNamePattern,
                @NotNull String driverClassName) {
    this.rdbms = rdbms;
    this.driverClassName = driverClassName;
    this.connectionStringPattern = Pattern.compile(connectionStringPattern, Pattern.CASE_INSENSITIVE);
    this.usualJarNamePattern = Pattern.compile(usualJarNamePattern, Pattern.CASE_INSENSITIVE);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    JdbcDriverDef driver = (JdbcDriverDef)o;

    if (!connectionStringPattern.equals(driver.connectionStringPattern)) return false;
    if (rdbms != driver.rdbms) return false;
    if (!usualJarNamePattern.equals(driver.usualJarNamePattern)) return false;

    return true;
  }


  @Override
  public int hashCode() {
    return rdbms.hashCode() * 7774777 + connectionStringPattern.hashCode();
  }
}
