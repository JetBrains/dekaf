package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.core.DekafSettingNames;
import org.jetbrains.dekaf.core.Settings;
import org.jetbrains.dekaf.exceptions.DBConnectionException;
import org.jetbrains.dekaf.inter.InterFacade;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import static org.jetbrains.dekaf.util.Objects.castTo;



final class JdbcFacade implements InterFacade {


    ////// STATE \\\\\\

    @Nullable
    final JdbcProvider provider;

    @NotNull
    final Rdbms rdbms;

    @NotNull
    final Specific specific;

    @NotNull
    private Settings settings = Settings.NO_SETTINGS;

    @Nullable
    private Driver driver = null;

    @Nullable
    private DataSource dataSource;

    private boolean active;

    @NotNull
    private final ArrayList<JdbcSession> sessions = new ArrayList<>();

    private ConnectionInfo connectionInfo = null;


    ////// LONG SERVICE \\\\\\

    JdbcFacade(final @Nullable JdbcProvider provider,
               final @NotNull Rdbms rdbms,
               final @NotNull Specific specific) {
        this.provider = provider;
        this.rdbms = rdbms;
        this.specific = specific;
    }

    @Override
    public void setUp(final @NotNull Settings settings) {
        this.settings = settings;
    }

    @Override
    public void shutDown() {

    }



    ////// TUNING \\\\\\

    @Override
    public @NotNull Rdbms getRdbms() {
        return this.rdbms;
    }



    ////// CONNECT-DISCONNECT \\\\\\


    @Override
    public void activate() {
        if (dataSource == null) {
            Driver driver = getDriver();
            String connectionString = settings.get(DekafSettingNames.ConnectionString);
            Properties properties = settings.toSubgroupProperties(null, DekafSettingNames.ConnectionParameterSection);
            JdbcSimpleDataSource ds = new JdbcSimpleDataSource(connectionString, properties, driver);
            dataSource = ds;
        }
        if (!active || connectionInfo == null) {
            active = true;
            obtainConnectionInfo();
        }
    }

    @NotNull
    Driver getDriver() {
        if (driver == null) {
            Settings driverSettings = settings.subSettings(DekafSettingNames.DriverSection);
            driver = JdbcMaster.ourDriverManager.getDriver(driverSettings);
        }
        return driver;
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

}
