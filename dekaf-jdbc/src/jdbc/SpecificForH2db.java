package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ConnectionInfo;

import java.sql.Connection;
import java.util.regex.Pattern;



final class SpecificForH2db extends Specific {

    private static final Pattern CONNECTION_STRING_PATTERN =
            Pattern.compile("^jdbc:h2:.+$", Pattern.CASE_INSENSITIVE);



    @Override
    protected Pattern getConnectionStringPattern() {
        return CONNECTION_STRING_PATTERN;
    }

    @Override
    protected String getDriverClassName() {
        return "org.h2.Driver";
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
            "select database(), schema(), nvl(current_user(), user())";



}
