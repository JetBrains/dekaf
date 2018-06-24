package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.Settings;
import org.jetbrains.dekaf.util.Version;

import java.sql.Driver;
import java.util.Objects;



/**
 * A wrapper for a JDBC driver.
 */
public final class JdbcDriverBox
{
    /**
     * Settings applied to the driver.
     */
    @NotNull
    public final Settings settings;

    /**
     * The classloader where the driver is loaded to.
     */
    @NotNull
    public final ClassLoader classLoader;

    /**
     * The JDBC driver itself.
     */
    @NotNull
    public final Driver driver;

    /**
     * Driver version;
     */
    @NotNull
    public final Version version;

    /**
     * Determines whether we have loaded this driver (true) or provided by the user (false).
     */
    public final boolean loadedByUs;

    /**
     * Determines whether this driver is registered into {@link java.sql.DriverManager}.
     */
    public final boolean registeredInJavaDriverManager;



    public JdbcDriverBox(@NotNull final Settings settings,
                         @NotNull final ClassLoader classLoader,
                         @NotNull final Driver driver,
                         @NotNull final Version version,
                         final boolean loadedByUs,
                         final boolean registeredInJavaDriverManager)
    {
        this.settings = settings;
        this.classLoader = classLoader;
        this.driver = driver;
        this.version = version;
        this.loadedByUs = loadedByUs;
        this.registeredInJavaDriverManager = registeredInJavaDriverManager;
    }



    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof JdbcDriverBox)) return false;
        JdbcDriverBox that = (JdbcDriverBox) o;
        return Objects.equals(this.settings, that.settings)
            && Objects.equals(this.classLoader, that.classLoader)
            && Objects.equals(this.driver, that.driver)
            && Objects.equals(this.version, that.version)
            && this.loadedByUs == that.loadedByUs
            && this.registeredInJavaDriverManager == that.registeredInJavaDriverManager;
    }

    @Override
    public int hashCode()
    {
        return classLoader.hashCode() * 101 + driver.hashCode();
    }
}
