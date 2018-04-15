package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.core.DekafSettingNames;
import org.jetbrains.dekaf.core.Settings;



/**
 *
 */
public class H2mem {

    public static final Settings settings = new Settings(
        DekafSettingNames.DriverClassName, "org.h2.Driver",
        DekafSettingNames.ConnectionString, "jdbc:h2:mem:TestDatabase"
    );



}
