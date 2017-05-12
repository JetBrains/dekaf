package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionParameterCategory;
import org.jetbrains.dekaf.exceptions.DBConnectionException;
import org.jetbrains.dekaf.inter.InterFacade;
import org.jetbrains.dekaf.inter.InterSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;



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
    private Properties properties;

    @Nullable
    private DataSource dataSource;

    private boolean active;

    @NotNull
    private final ArrayList<JdbcSession> sessions = new ArrayList<>();


    JdbcFacade(final @Nullable JdbcProvider provider,
               final @NotNull Rdbms rdbms,
               final @NotNull Specific specific) {
        this.provider = provider;
        this.rdbms = rdbms;
        this.specific = specific;
        this.connectionString = null;
        this.properties = new Properties();
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
        if (dataSource != null) {
            if (provider == null) throw new IllegalStateException("Neither provider nor data source specified — impossible to activate");
            Driver driver = provider.getDriver(specific.getDriverClassName());
            JdbcSimpleDataSource ds = new JdbcSimpleDataSource(connectionString, properties, driver);
            dataSource = ds;
        }
        if (!active) {
            active = true;
        }
    }

    @Override
    public void deactivate() {
        if (!active) return;
    }

    @Override
    public boolean isActive() {
        return active;
    }


    //////  SESSIONS \\\\\\


    @Override
    public @NotNull InterSession openSession(final @Nullable String databaseName,
                                             final @Nullable String userName,
                                             final @Nullable String password) {
        Connection connection = obtainConnection();
        JdbcSession session = new JdbcSession(this, connection);
        sessions.add(session);
        return session;
    }

    private Connection obtainConnection() {
        try {
            return dataSource.getConnection();
        }
        catch (SQLException e) {
            throw new DBConnectionException(e, null);
        }
    }
}
