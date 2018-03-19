package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ConnectionInfo;

import java.sql.Connection;
import java.util.regex.Pattern;



final class SpecificForOracle extends Specific {

    private static final Pattern CONNECTION_STRING_PATTERN =
            Pattern.compile("^jdbc:oracle:(oci|thin):.*@.+$", Pattern.CASE_INSENSITIVE);



    @Override
    protected Pattern getConnectionStringPattern() {
        return CONNECTION_STRING_PATTERN;
    }

    @Override
    protected String getDriverClassName() {
        return "oracle.jdbc.driver.OracleDriver";
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
            "select sys_context('userenv', 'db_name') as database_name,      \n" +
            "       sys_context('userenv', 'current_schema') as schema_name, \n" +
            "       user as user_name                                        \n" +
            "from dual                                                       \n";

}
