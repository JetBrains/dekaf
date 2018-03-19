package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.DekafSettingNames;
import org.jetbrains.dekaf.core.Settings;
import org.jetbrains.dekaf.exceptions.JdbcDriverLoadingException;
import org.jetbrains.dekaf.util.JarLoader;
import org.jetbrains.dekaf.util.Version;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.jetbrains.dekaf.util.Strings.NO_STRINGS;



final class JdbcDriverManager {

    ////// APPLICABLE SETTINGS \\\\\\




    ////// STATE \\\\\\

    /**
     * All loaded JDBC drivers in their boxes.
     */
    @NotNull
    private final ConcurrentMap<Settings, JdbcDriverBox> boxes;

    /**
     * Our base class loader.
     */
    @NotNull
    private final ClassLoader baseClassLoader;

    /**
     * Our default (guessed) driver directory.
     */
    @NotNull
    private String defaultDriverDirectory;

    @NotNull
    private transient JarLoader loader;


    ////// CONSTRUCTORS \\\\\\

    JdbcDriverManager() {
        baseClassLoader = JdbcMaster.class.getClassLoader();
        defaultDriverDirectory = guessDriverDirectory();
        boxes = new ConcurrentHashMap<>();
        loader = new JarLoader(baseClassLoader);
    }


    ////// DRIVERS \\\\\\

    @NotNull
    public synchronized JdbcDriverBox loadDriver(final @NotNull Settings settings) {
        String className = settings.get(DekafSettingNames.DriverClassName);
        if (className == null) throw new JdbcDriverLoadingException("Driver class is not specified");
        boolean register = settings.getBoolean(DekafSettingNames.JdbcRegisterDriver, false);

        String[] jarNames = splitJarString(settings.get(DekafSettingNames.DriverJarsNames));
        final JdbcDriverBox box;
        if (jarNames.length > 0) {
            // loading the requested driver
            String directory = settings.get(DekafSettingNames.DriverJarsDirectory, defaultDriverDirectory);
            Path dir = Paths.get(directory);
            ClassLoader classLoader = loader.load(dir, jarNames);
            Driver driver = createDriver(classLoader, className, register);

            box = new JdbcDriverBox(
                settings,
                classLoader,
                driver,
                Version.ZERO, // TODO
                true,
                register
            );
        }
        else {
            // try to get already attached driver
            Driver driver = createDriver(baseClassLoader, className, register);

            box = new JdbcDriverBox(
                settings,
                baseClassLoader,
                driver,
                Version.ZERO, // TODO
                true,
                register
            );
        }
        
        boxes.put(settings, box);
        return box;
    }

    @NotNull
    private static Driver createDriver(final @NotNull ClassLoader classLoader,
                                       final @NotNull String className,
                                       boolean register)
    {
        try {
            Class<?> objectClass = classLoader.loadClass(className);
            if (!(Driver.class.isAssignableFrom(objectClass)))
                throw new JdbcDriverLoadingException("Wrong driver class: " + objectClass);
            @SuppressWarnings("unchecked")
            Class<Driver> driverClass = (Class<Driver>) objectClass;
            Driver driver = driverClass.newInstance();
            if (register) DriverManager.registerDriver(driver);
            return driver;
        }
        catch (ClassNotFoundException e) {
            throw new JdbcDriverLoadingException("Driver class not found: " + className, e);
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new JdbcDriverLoadingException("Failed to instantiate class: " + className, e);
        }
        catch (SQLException e) {
            throw new JdbcDriverLoadingException(e);
        }
    }

    @NotNull
    private static String[] splitJarString(final @Nullable String string) {
        if (string == null || string.length() == 0) return NO_STRINGS;
        String[] items = string.split(",");
        for (int i = 0; i < items.length; i++) items[i] = items[i].trim();
        return items;
    }

    @NotNull
    public Driver getDriver(final @NotNull Settings settings) {
        JdbcDriverBox box = findDriverBox(settings);
        if (box == null) box = loadDriver(settings);
        return box.driver;
    }

    @Nullable
    public Driver findDriver(final @NotNull Settings settings) {
        JdbcDriverBox box = findDriverBox(settings);
        return box != null ? box.driver : null;
    }

    @Nullable
    public JdbcDriverBox findDriverBox(final @NotNull Settings settings) {
        return boxes.get(settings);
    }


    private static String guessDriverDirectory() {
        return "lib/jdbc"; // TODO detect default drivers directory
    }



    synchronized void deregisterAllDrivers() {
        // TODO
    }

}
