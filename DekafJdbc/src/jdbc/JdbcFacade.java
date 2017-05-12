package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionParameterCategory;
import org.jetbrains.dekaf.inter.InterFacade;
import org.jetbrains.dekaf.inter.InterSession;

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
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }


    //////  SESSIONS \\\\\\


    @Override
    public @NotNull InterSession openSession(final @Nullable String databaseName,
                                             final @Nullable String userName,
                                             final @Nullable String password) {
        throw new RuntimeException("Not implemented yet");
    }
}
