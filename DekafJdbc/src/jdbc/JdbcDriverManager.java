package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBDriverException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;



final class JdbcDriverManager {

    ////// STATE \\\\\\

    @Nullable
    private final JdbcDriverManager parentDriverManager;

    @NotNull
    private final ClassLoader parentClassLoader;

    @Nullable
    private URLClassLoader driverClassLoader = null;

    @NotNull
    private String driverDirectory = guessDriverDirectory();

    @Nullable
    private Collection<String> driverJars = null;

    @NotNull
    private final Map<String, Driver> drivers = new TreeMap<>();


    ////// CONSTRUCTORS \\\\\\

    JdbcDriverManager() {
        this((ClassLoader)null);
    }

    JdbcDriverManager(@Nullable final ClassLoader parentClassLoader) {
        this.parentDriverManager = null;
        this.parentClassLoader =
                parentClassLoader != null
                        ? parentClassLoader
                        : this.getClass().getClassLoader();
    }

    JdbcDriverManager(@NotNull final JdbcDriverManager parentDriverManager) {
        this.parentDriverManager = parentDriverManager;
        this.parentClassLoader = parentDriverManager.getDriverClassLoader();
    }


    ////// DRIVERS \\\\\\

    @NotNull
    synchronized String getDriverDirectory() {
        return driverDirectory;
    }

    synchronized void setDriverDirectory(@NotNull final String driverDirectory) {
        this.driverDirectory = driverDirectory;
    }

    private static String guessDriverDirectory() {
        return "lib/jdbc"; // TODO detect default drivers directory
    }

    @Nullable
    synchronized Collection<String> getDriverJars() {
        return driverJars;
    }

    synchronized void setDriverJars(@Nullable final Collection<String> driverJars) {
        this.driverJars = driverJars;
    }

    @NotNull
    private synchronized ClassLoader getDriverClassLoader() {
        if (driverClassLoader == null) initDriversClassLoader();
        ClassLoader classLoader = driverClassLoader;
        assert classLoader != null;
        return classLoader;
    }

    private /*synchronized*/ void initDriversClassLoader() {
        if (driverClassLoader != null) return;

        URL[] urls = new URL[0];
        if (driverJars != null) {
            File dir = new File(driverDirectory);
            ArrayList<URL> us = new ArrayList<>(urls.length);
            for (String j : driverJars) {
                boolean abs = j.startsWith("/") || j.startsWith(File.separator);
                File f = abs ? new File(j) : new File(dir, j);
                us.add(fileToURL(f));
            }
            urls = us.toArray(urls);
        }

        ClassLoader myClassLoader = new URLClassLoader(urls, parentClassLoader);
    }

    private static URL fileToURL(File f) {
        try {
            return f.toURI().toURL();
        }
        catch (MalformedURLException e) {
            // TODO handle it somehow
            throw new RuntimeException(e.toString(), e);
        }
    }


    @NotNull
    synchronized Driver getDriver(@NotNull String className) {
        Driver driver = drivers.get(className);
        if (driver == null && parentDriverManager != null) {
            driver = parentDriverManager.getDriver(className);
        }
        if (driver == null) {
            Class<Driver> driverClass = getDriverClass(className);
            driver = instantiateDriver(driverClass);
            drivers.put(className, driver);
        }
        return driver;
    }

    @NotNull
    private Class<Driver> getDriverClass(final @NotNull String className)  {
        final ClassLoader classLoader = getDriverClassLoader();

        final Class justClass;
        try {
            justClass = classLoader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new DBDriverException("Cannot load JDBC driver: no class "+className, e);
        }
        catch (Exception e) {
            String msg = "Failed to load class "+className+": "+e.getMessage();
            throw new DBDriverException(msg, e);
        }

        if (Driver.class.isAssignableFrom(justClass)) {
            //noinspection unchecked
            return justClass;
        }
        else {
            throw new IllegalArgumentException("The class "+justClass.getName()+" is not a valid JDBC driver");
        }
    }

    @NotNull
    private static Driver instantiateDriver(final @NotNull Class<Driver> driverClass) {
        try {
            return driverClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw new DBDriverException("Failed to instantiate driver class "+driverClass.getName()+": "+e.getMessage(), e);
        }
    }


    synchronized void deregisterAllDrivers() {
        for (Driver driver : drivers.values()) {
            // TODO deregister the driver
        }
        drivers.clear();
    }

}
