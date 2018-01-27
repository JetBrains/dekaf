package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.core.ConnectionParameterCategory;
import org.jetbrains.dekaf.core.DbDriverInfo;
import org.jetbrains.dekaf.core.ImplementationAccessibleService;
import org.jetbrains.dekaf.exceptions.DBConnectionException;
import org.jetbrains.dekaf.inter.InterFacade;
import org.jetbrains.dekaf.util.Version;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import static org.jetbrains.dekaf.util.Objects.castTo;



final class JdbcFacade implements InterFacade {

    @Nullable
    final JdbcProvider provider;

    @NotNull
    final Rdbms rdbms;

    @NotNull
    final Specific specific;

    @Nullable
    private String connectionString;

    @NotNull
    final private Properties properties = new Properties();

    @Nullable
    private DataSource dataSource;

    private boolean active;

    @NotNull
    private final ArrayList<JdbcSession> sessions = new ArrayList<>();

    @Nullable
    private Driver driver = null;

    @Nullable
    private DbDriverInfo driverInfo = null;

    @Nullable
    private ConnectionInfo connectionInfo = null;


    JdbcFacade(final @Nullable JdbcProvider provider,
               final @NotNull Rdbms rdbms,
               final @NotNull Specific specific) {
        this.provider = provider;
        this.rdbms = rdbms;
        this.specific = specific;
        this.connectionString = null;
    }



    ////// TUNING \\\\\\

    @Override
    public void setJarsPath(final @Nullable String path) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void setJarsToLoad(final @Nullable String[] files) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void setConnectionString(final @Nullable String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public void setParameters(final @NotNull ConnectionParameterCategory category,
                              final @NotNull Map<String, Object> parameters) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public @NotNull Rdbms getRdbms() {
        return this.rdbms;
    }



    ////// CONNECT-DISCONNECT \\\\\\


    @Override
    public void activate() {
        if (driver == null) {
            activateDriver();
            assert driver != null : "JDBC driver has not been activated";
            assert driverInfo != null : "JDBC driver info is omitted";
        }

        if (dataSource == null) {
            JdbcSimpleDataSource ds = new JdbcSimpleDataSource(connectionString, properties, driver);
            dataSource = ds;
        }
        if (!active || connectionInfo == null) {
            active = true;
            obtainConnectionInfo();
        }
    }

    public void activateDriver() {
        driver = ensureDriver();
        driverInfo = obtainDriverInfo(driver);
    }

    @NotNull
    Driver ensureDriver() {
        if (provider == null) throw new IllegalStateException("Neither provider nor data source specified — impossible to activate");
        return provider.getDriver(specific.getDriverClassName());
    }

    @NotNull
    private static DbDriverInfo obtainDriverInfo(final @Nullable Driver driver) {
        String name = driver.toString();
        Version version = Version.of(driver.getMajorVersion(), driver.getMinorVersion());
        return new DbDriverInfo(name, version);
    }


    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void deactivate() {
        if (!active) return;
        if (!sessions.isEmpty()) closeAllSessions();
        active = false;
    }

    private void closeAllSessions() {
        JdbcSession[] sessionsToClose = this.sessions.toArray(new JdbcSession[0]);
        for (int i = sessionsToClose.length-1; i >= 0; i--) {
            sessionsToClose[i].close();
        }
    }

    private void obtainConnectionInfo() {
        Connection connection = obtainConnection();
        try {
            connectionInfo = specific.obtainConnectionInfo(connection);
        }
        finally {
            JdbcUtil.close(connection);
        }
    }


    @Override
    public void deactivateDriver() {
        deactivate();

        driverInfo = null;
        driver = null;
    }

    @Override
    public @Nullable DbDriverInfo getDriverInfo() {
        return driverInfo;
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }


    //////  SESSIONS \\\\\\


    @Override
    public JdbcSession openSession() {
        Connection connection = obtainConnection();
        JdbcSession session = new JdbcSession(this, connection);
        sessions.add(session);
        return session;
    }

    @NotNull
    Connection obtainConnection() {
        DataSource ds = dataSource;
        assert ds != null : "DataSource is not initialized";
        try {
            Connection connection = ds.getConnection();
            assert connection != null : "Connection is not obtained";
            return connection;
        }
        catch (SQLException e) {
            throw new DBConnectionException(e, null);
        }
    }

    void sessionClosed(final @NotNull JdbcSession session) {
        sessions.remove(session);
    }


    ////// DIAGNOSTIC FUNCTIONS \\\\\\

    int countSessions() {
        return sessions.size();
    }


    ////// IMPLEMENTATION SERVICES \\\\\\


    @Override @SuppressWarnings("unchecked")
    public <I> @Nullable I getSpecificService(final @NotNull Class<I> serviceClass,
                                              final @NotNull String serviceName)
            throws ClassCastException
    {
        switch (serviceName) {
            case Names.JDBC_DATA_SOURCE: return castTo(serviceClass, dataSource);
            case Names.JDBC_DRIVER:      return castTo(serviceClass, getDriver());
            default:                     return null;
        }
    }

    private @Nullable Driver getDriver() {
        if (dataSource != null && dataSource instanceof ImplementationAccessibleService) {
            ImplementationAccessibleService service = (ImplementationAccessibleService) dataSource;
            return service.getSpecificService(Driver.class, Names.JDBC_DRIVER);
        }
        else {
            return null;
        }
    }

}
