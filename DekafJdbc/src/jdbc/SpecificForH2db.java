package org.jetbrains.dekaf.jdbc;

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


}
