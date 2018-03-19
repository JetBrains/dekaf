package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.exceptions.UnknownDBException;
import org.jetbrains.dekaf.util.Version;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.regex.Pattern;



final class SpecificForSybase extends Specific {

    private static final Pattern CONNECTION_STRING_PATTERN =
            Pattern.compile("^jdbc:(jtds:)?sybase:(Tds:)?.+$", Pattern.CASE_INSENSITIVE);



    @Override
    protected Pattern getConnectionStringPattern() {
        return CONNECTION_STRING_PATTERN;
    }

    @Override
    protected String getDriverClassName() {
        return "net.sourceforge.jtds.jdbc.Driver";
    }


    @Override
    public ConnectionInfo obtainConnectionInfoNatively(final @NotNull Connection connection) {
        String[] env;
        String rdbmsName, driverVersionStr;

        // retrieving all except driver version
        env = queryOneRowOfStrings(connection, CONNECTION_INFO_QUERY, 4);

        // getting the driver version
        try {
            DatabaseMetaData md = connection.getMetaData();
            rdbmsName = md.getDatabaseProductName();
            if (rdbmsName == null) rdbmsName = connection.getClass().getName();
            driverVersionStr = md.getDriverVersion();
        }
        catch (SQLException sqle) {
            throw new UnknownDBException(sqle, "getting versions");
            //throw getExceptionRecognizer().recognizeException(sqle, "getting versions using JDBC metadata");
        }

        Version driverVersion =
                extractVersion(driverVersionStr, SIMPLE_VERSION_PATTERN, 1);

        if (env != null) {
            assert env.length == 4;
            String serverVersionStr = env[3];
            Version serverVersion =
                    extractVersion(serverVersionStr, SYBASE_ASE_VERSION_PATTERN, 1);
            return new ConnectionInfo(rdbmsName, env[0], env[1], env[2], serverVersion, driverVersion);
        }
        else {
            return new ConnectionInfo(rdbmsName, null, null, null, Version.ZERO, driverVersion);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    static final String CONNECTION_INFO_QUERY =
            "select db_name(), user_name(), suser_name(), @@version";

    static final Pattern SYBASE_ASE_VERSION_PATTERN =
            Pattern.compile("/(\\d{2}(\\.\\d{1,3}){1,5})/");


}
