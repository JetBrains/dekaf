package org.jetbrains.dekaf.jdbc;

import java.util.regex.Pattern;



final class SpecificForPostgres extends Specific {

    private static final Pattern CONNECTION_STRING_PATTERN =
            Pattern.compile("^jdbc:postgresql://.+$", Pattern.CASE_INSENSITIVE);



    @Override
    protected Pattern getConnectionStringPattern() {
        return CONNECTION_STRING_PATTERN;
    }


}
