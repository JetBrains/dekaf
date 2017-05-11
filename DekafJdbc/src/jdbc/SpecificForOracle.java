package org.jetbrains.dekaf.jdbc;

import java.util.regex.Pattern;



final class SpecificForOracle extends Specific {

    private static final Pattern CONNECTION_STRING_PATTERN =
            Pattern.compile("^jdbc:oracle:(oci|thin):.*@.+$", Pattern.CASE_INSENSITIVE);



    @Override
    protected Pattern getConnectionStringPattern() {
        return CONNECTION_STRING_PATTERN;
    }


}
