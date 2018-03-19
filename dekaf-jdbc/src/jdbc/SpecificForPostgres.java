package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ConnectionInfo;

import java.sql.Connection;
import java.util.regex.Pattern;



final class SpecificForPostgres extends Specific {

    private static final Pattern CONNECTION_STRING_PATTERN =
            Pattern.compile("^jdbc:postgresql://.+$", Pattern.CASE_INSENSITIVE);



    @Override
    protected Pattern getConnectionStringPattern() {
        return CONNECTION_STRING_PATTERN;
    }

    @Override
    protected String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public ConnectionInfo obtainConnectionInfoNatively(final @NotNull Connection connection) {
        return getConnectionInfoSmartly(connection,
                                        CONNECTION_INFO_QUERY,
                                        SIMPLE_VERSION_PATTERN, 1,
                                        SIMPLE_VERSION_PATTERN, 1);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static final String CONNECTION_INFO_QUERY =
            "select current_database(), current_schema(), current_user";


}
