package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.util.Version;

import java.io.Serializable;



/**
 * Brief connection info.
 *
 * Value object.
 *
 * @author Leonid Bushuev from JetBrains
 **/
public final class ConnectionInfo implements Serializable {

  @Nullable
  public final String databaseName;

  @Nullable
  public final String schemaName;

  @Nullable
  public final String userName;

  @NotNull
  public final Version serverVersion;

  @NotNull
  public final Version driverVersion;


  public ConnectionInfo(@Nullable final String databaseName,
                        @Nullable final String schemaName,
                        @Nullable final String userName,
                        @NotNull final Version serverVersion,
                        @NotNull final Version driverVersion) {
    this.databaseName = databaseName;
    this.schemaName = schemaName;
    this.userName = userName;
    this.serverVersion = serverVersion;
    this.driverVersion = driverVersion;
  }


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ConnectionInfo that = (ConnectionInfo) o;

    return !(databaseName != null ? !databaseName.equals(that.databaseName) : that.databaseName != null)
        && !(schemaName != null ? !schemaName.equals(that.schemaName) : that.schemaName != null)
        && !(userName != null ? !userName.equals(that.userName) : that.userName != null)
        && serverVersion.equals(that.serverVersion)
        && driverVersion.equals(that.driverVersion);
  }

  @Override
  public int hashCode() {
    int result = databaseName != null ? databaseName.hashCode() : 0;
    result = 31 * result + (schemaName != null ? schemaName.hashCode() : 0);
    result = 31 * result + (userName != null ? userName.hashCode() : 0);
    result = 31 * result + serverVersion.hashCode();
    return result;
  }
}
