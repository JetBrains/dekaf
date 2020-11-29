Settings
--------

Format of settings, passed to an RDBMS service facade.


### Driver settings
                      
Prefix: ``driver``

This settings specify how to load JDBC driver.

| setting     | description |
| -------     | ----------- |
| ``path``    | Path to the driver jar files. |
| ``jars``    | Names of the jar files to load, comma separated. |
| ``class``   | Jdbc driver class (should implement interface ``java.sql.Driver``). |


### JDBC setting

Prefix: ``jdbc`` 

| setting     | description |
| -------     | ----------- |
| ``connection-string``    | The JDBC connection string (a.k.a. JDBC URL). |
       

Prefix: ``jdbc.properties``

All JDBC-specific properties that are passed to the JDBC driver as is.

