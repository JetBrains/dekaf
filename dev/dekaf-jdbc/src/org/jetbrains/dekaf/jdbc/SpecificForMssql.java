package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ConnectionInfo;

import java.sql.Connection;
import java.util.regex.Pattern;



final class SpecificForMssql extends Specific {

    /**
     * <p>
     * See <a href="https://msdn.microsoft.com/en-us/library/ms378428(v=sql.110).aspx">MS SQL: Building the Connection URL</a>
     * </p>
     */
    private static final Pattern CONNECTION_STRING_PATTERN =
            Pattern.compile("^jdbc:sqlserver:.+$", Pattern.CASE_INSENSITIVE);



    @Override
    protected Pattern getConnectionStringPattern() {
        return CONNECTION_STRING_PATTERN;
    }

    @Override
    protected String getDriverClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }


    @Override
    public ConnectionInfo obtainConnectionInfoNatively(final @NotNull Connection connection) {
        return getConnectionInfoSmartly(connection,
                                        CONNECTION_INFO_QUERY,
                                        SIMPLE_VERSION_PATTERN, 1,
                                        SIMPLE_VERSION_PATTERN, 1);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CONNECTION_INFO_QUERY =
            "select db_name(), schema_name(), original_login()";



}
