package org.jetbrains.dekaf.jdbc.drivers;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.URLClassLoader;
import java.sql.Driver;



public final class JdbcDriver implements Closeable {

    public final @NotNull Class<Driver> driverClass;

    public final @NotNull URLClassLoader driverClassLoader;

    public JdbcDriver(@NotNull final Class<Driver> driverClass,
                      @NotNull final URLClassLoader driverClassLoader)
    {
        this.driverClass = driverClass;
        this.driverClassLoader = driverClassLoader;
    }

    @Override
    public void close() {
        try {
            driverClassLoader.close();
        }
        catch (IOException e) {
            // I have no idea how why closing a class loader can throw exceptions
            // and what can I do in this case.
            System.err.println("PANIC: driverClassLoader.close() throws an unexpected exception: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return driverClass.toString();
    }
}
