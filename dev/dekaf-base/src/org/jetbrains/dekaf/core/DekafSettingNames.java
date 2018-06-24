package org.jetbrains.dekaf.core;

/**
 * Possible Dekaf settings.
 */
public interface DekafSettingNames {

    /// SECTIONS \\\

    String DriverSection               = "driver";
    String ConnectionSection           = "connection";
    String ConnectionParameterSection  = "connection.parameter";


    /// COMMON \\\

    String DriverJarsDirectory      = "driver.jars.directory";
    String DriverJarsNames          = "driver.jars.names";
    String DriverClassName          = "driver.class.name";

    String ConnectionString         = "connection.string";


    /// JDBC \\\

    String JdbcRegisterDriver       = "jdbc.registerDriver";

}
