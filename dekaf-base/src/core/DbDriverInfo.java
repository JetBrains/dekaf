package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.util.Version;

import java.io.Serializable;



/**
 * Brief connection info.
 *
 * Value object.
 *
 * @author Leonid Bushuev from JetBrains
 **/
public final class DbDriverInfo implements Serializable {

    @NotNull
    public final String driverName;

    @NotNull
    public final Version driverVersion;


    public DbDriverInfo(@NotNull final String driverName,
                        @NotNull final Version driverVersion) {
        this.driverName = driverName;
        this.driverVersion = driverVersion;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbDriverInfo that = (DbDriverInfo) o;

        return driverName.equals(that.driverName)
            && driverVersion.equals(that.driverVersion);
    }


    @Override
    public int hashCode() {
        return driverName.hashCode() * 101 + driverVersion.hashCode();
    }
}
