package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.exceptions.DBDriverException;
import org.jetbrains.dekaf.inter.exceptions.DBInitializationException;
import org.jetbrains.dekaf.inter.intf.InterFacade;
import org.jetbrains.dekaf.inter.settings.Settings;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;



public class JdbcFacade implements InterFacade {

    @NotNull
    protected final JdbcServiceFactory factory;

    /// SETTINGS \\\

    @Nullable
    private String driverPath = null;

    @Nullable
    private String[] driverJars = null;

    @Nullable
    private String driverClassName = null;

    @Nullable
    private String jdbcConnectionString = null;

    @Nullable
    private Settings jdbcParameters = null;


    /// CONFIGURATION \\\

    private ClassLoader driverClassLoader = this.getClass().getClassLoader();

    private Driver driver = null;



    /// STATE \\\

    private final ArrayList<JdbcSession> sessions = new ArrayList<>();



    /// INITIALIZATION \\\



    protected JdbcFacade(final @NotNull JdbcServiceFactory factory) {
        this.factory = factory;
    }

    @Override
    public void init(final @NotNull Settings settings)
            throws DBInitializationException
    {
        Settings ds = settings.getNest("driver");
        if (ds != null) {
            driverPath = ds.getString("path");
            driverJars = ds.getStrings("jars");
            driverClassName = ds.getString("class");
        }
        else {
            driverJars = null;
            driverClassName = null;
        }

        Settings js = settings.getNest("jdbc");
        if (js != null) {
            jdbcConnectionString = js.getString("connection-string");
            jdbcParameters = js.getNest("parameters");
        }
        else {
            jdbcConnectionString = null;
            jdbcParameters = null;
        }

        obtainDriver();
    }

    private void obtainDriver()
            throws DBInitializationException
    {
        driver = null;

        if (driverClassName == null)
            throw new DBInitializationException("Driver class name is not specified");

        Class<? extends Driver> driverClass;
        try {
            Class<?> theClass = driverClassLoader.loadClass(driverClassName);
            driverClass = theClass.asSubclass(Driver.class);
        }
        catch (ClassNotFoundException cnfe) {
            throw new DBInitializationException("Cannot load driver: no driver class: " + driverClassName, cnfe);
        }
        catch (ClassCastException cce) {
            throw new DBInitializationException("Cannot load driver: the given class is not a driver: " + driverClassName, cce);
        }

        try {
            driver = driverClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e) {
            String exceptionName = e.getClass().getSimpleName();
            String exceptionMessage = e.getMessage();
            String message = "Cannot instantiate driver: exception " + exceptionName + ": " + exceptionMessage;
            throw new DBInitializationException(message, e);
        }
    }



    /// SESSIONS \\\

    @Override @NotNull
    public JdbcSession openSession() {
        return openSession(null, null);
    }

    @Override @NotNull
    public JdbcSession openSession(final @Nullable String connectionString,
                                   final @Nullable Settings connectionParameters) {
        if (driver == null) throw new IllegalStateException("Facade is not initialized");

        Connection connection = obtainConnection(connectionString, connectionParameters);

        JdbcSession session = factory.createSession(this);
        session.init(connection);

        synchronized (sessions) {
            sessions.add(session);
        }

        return session;
    }

    @ApiStatus.Internal
    public Connection obtainConnection(final @Nullable String connectionString,
                                       final @Nullable Settings connectionParameters) {
        String cs = connectionString != null ? connectionString : this.jdbcConnectionString;
        Settings ps = connectionParameters != null ? connectionParameters : this.jdbcParameters;

        Connection connection;
        try {
            if (connectionString != null) {
                if (!driver.acceptsURL(connectionString))
                    throw new DBDriverException("Jdbc Driver doesn't accept this connection string: " + connectionString);
            }
            Properties props = ps != null ? ps.toProperties() : new Properties();
            connection = driver.connect(cs, props);
        }
        catch (SQLException sqle) {
            throw new DBDriverException("Failed to connect: " + sqle.getMessage(), sqle);
        }
        catch (Exception e) {
            throw new DBDriverException("Failed to connect: " + e.getMessage(), e);
        }
        return connection;
    }

    @ApiStatus.Internal
    public void releaseConnection(final @NotNull Connection connection) {
        JdbcUtil.close(connection);
    }

    @Override
    public int getSessionsCount() {
        synchronized (sessions) {
            return sessions.size();
        }
    }

    @Override
    public void closeAllSessions() {
        JdbcSession[] sessionsToClose;
        synchronized (sessions) {
            sessionsToClose = sessions.toArray(new JdbcSession[0]);
            sessions.clear();
        }
        for (int i = sessionsToClose.length - 1; i >= 0; i--) {
            sessionsToClose[i].close();
        }
    }

    void sessionJustClosed(final JdbcSession session) {
        sessions.remove(session);
    }

}
