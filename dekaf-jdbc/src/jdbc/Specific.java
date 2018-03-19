package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.exceptions.DBException;
import org.jetbrains.dekaf.exceptions.UnknownDBException;
import org.jetbrains.dekaf.util.Version;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



abstract class Specific {

    /// DRIVERS AND CLASSES \\\

    protected abstract Pattern getConnectionStringPattern();

    protected abstract String getDriverClassName();


    /// TRANSACTIONS \\\

    void transactionBegin(@NotNull Connection connection) throws SQLException {
        connection.setAutoCommit(false);
    }

    void transactionCommit(@NotNull Connection connection) throws SQLException {
        connection.commit();
    }

    void transactionRollback(@NotNull Connection connection) throws SQLException {
        connection.rollback();
    }

    void ping(final @NotNull Connection connection) throws SQLException {
        final String query = pingQuery();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rset = statement.executeQuery(query)) {
                rset.next();
            }
        }
    }

    String pingQuery() {
        return "select 1";
    }


    /// CONNECTION INFO \\\

    @NotNull
    public ConnectionInfo obtainConnectionInfo(final @NotNull Connection connection) {
        ConnectionInfo info = null;
        try {
            info = obtainConnectionInfoNatively(connection);
        }
        catch (DBException e) {
            // TODO log the exception if in debug mode
        }

        if (info == null) {
            info = obtainConnectionInfoFromJdbc(connection);
        }

        return info;
    }

    @Nullable
    protected ConnectionInfo obtainConnectionInfoNatively(final @NotNull Connection connection) {
        // inheritors should override this method
        return null;
    }

    @Nullable
    protected String getConnectionInfoQuery() {
        return null;
    }

    @NotNull
    protected ConnectionInfo obtainConnectionInfoFromJdbc(final @NotNull Connection connection) {
        try {
            DatabaseMetaData md = connection.getMetaData();
            String rdbmsName = md.getDatabaseProductName();
            if (rdbmsName == null) rdbmsName = connection.getClass().getName();
            String databaseName = connection.getCatalog();
            String schemaName = getSchema(connection);
            String userName = md.getUserName();
            Version serverVersion = Version.of(md.getDatabaseMajorVersion(), md.getDatabaseMinorVersion());
            Version driverVersion = Version.of(md.getDriverMajorVersion(), md.getDriverMinorVersion());
            return new ConnectionInfo(rdbmsName,
                                      databaseName, schemaName, userName,
                                      serverVersion, driverVersion);
        }
        catch (SQLException sqle) {
            throw new UnknownDBException("Failed to get brief connection info using JDBC connection metadata: "+sqle.getMessage(),
                                         sqle,
                                         "getting brief connection info using JDBC connection metadata");
            //throw  myExceptionRecognizer.recognizeException(sqle, "getting brief connection info using JDBC connection metadata");
        }
    }

    /**
     * Try to call the "Connection#getSchema()" function
     * that appears in JRE 1.7.
     *
     * Some JDBC vendor can also ignore this method or throw exceptions.
     */
    @Nullable
    protected static String getSchema(final @NotNull Connection connection) {
        String schemaName = null;
        try {
            final Class<? extends Connection> connectionClass = connection.getClass();
            final Method getSchemaMethod = connectionClass.getMethod("getSchema");
            if (getSchemaMethod != null) {
                schemaName = (String) getSchemaMethod.invoke(connection);
            }
        }
        catch (NoSuchMethodException nsm) {
            // no such method. sad
        }
        catch (Exception e) {
            // TODO log this somehow?
        }
        return schemaName;
    }


    @NotNull
    protected ConnectionInfo getConnectionInfoSmartly(final Connection connection,
                                                      final String envQuery,
                                                      final Pattern serverVersionPattern,
                                                      final int serverVersionGroupIndex,
                                                      final Pattern driverVersionPattern,
                                                      final int driverVersionGroupIndex) {
        // environment
        String[] env = queryOneRowOfStrings(connection, envQuery, 3);
        if (env == null) env = new String[] {null,null,null};

        // versions
        String rdbmsName, serverVersionStr, driverVersionStr;
        try {
            DatabaseMetaData md = connection.getMetaData();
            rdbmsName = md.getDatabaseProductName();
            if (rdbmsName == null) rdbmsName = connection.getClass().getName();
            serverVersionStr = md.getDatabaseProductVersion();
            driverVersionStr = md.getDriverVersion();
        }
        catch (SQLException sqle) {
            throw new UnknownDBException(sqle, "getting versions using JDBC metadata");
            //throw getExceptionRecognizer().recognizeException(sqle, "getting versions using JDBC metadata");
        }

        Version serverVersion =
                extractVersion(serverVersionStr, serverVersionPattern, serverVersionGroupIndex);
        Version driverVersion =
                extractVersion(driverVersionStr, driverVersionPattern, driverVersionGroupIndex);

        // ok
        return new ConnectionInfo(rdbmsName, env[0], env[1], env[2], serverVersion, driverVersion);
    }

    @Nullable
    protected String[] queryOneRowOfStrings(final @NotNull Connection connection,
                                            final @NotNull String queryText,
                                            int n) {
        try {
            PreparedStatement stmt = connection.prepareStatement(queryText);
            try {
                ResultSet rset = stmt.executeQuery();
                try {
                    if (rset.next()) {
                        String[] result = new String[n];
                        for (int i = 0; i < n; i++) result[i] = rset.getString(i + 1);
                        return result;
                    }
                    else {
                        return null;
                    }
                }
                finally {
                    JdbcUtil.close(rset);
                }
            }
            finally {
                JdbcUtil.close(stmt);
            }
        }
        catch (SQLException sqle) {
            throw new UnknownDBException(sqle, "getting versions using JDBC metadata");
            // TODO recognize
        }
    }


    protected static final Pattern SIMPLE_VERSION_PATTERN =
            Pattern.compile("(\\d{1,2}(\\.\\d{1,3}){1,5})");

    @NotNull
    protected static Version extractVersion(@Nullable final String serverVersionStr,
                                            @NotNull final Pattern versionPattern,
                                            final int groupIndex) {
        if (serverVersionStr == null || serverVersionStr.isEmpty()) return Version.ZERO;
        Matcher m = versionPattern.matcher(serverVersionStr);
        if (m.find()) return Version.of(m.group(groupIndex));
        else return Version.ZERO;
    }



}
