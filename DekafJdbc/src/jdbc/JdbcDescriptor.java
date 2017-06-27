package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.*;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;



enum JdbcDescriptor {

    JDBC_H2(H2db.RDBMS, new SpecificForH2db()),
    JDBC_POSTGRES(Postgres.RDBMS, new SpecificForPostgres()),
    JDBC_ORACLE(Oracle.RDBMS, new SpecificForOracle()),
    JDBC_MSSQL(Mssql.RDBMS, new SpecificForMssql()),
    JDBC_SYBASE(Sybase.RDBMS, new SpecificForSybase()),
    JDBC_SQLITE(Sqlite.RDBMS, new SpecificForSqlite());

    final Rdbms    rdbms;
    final Specific specific;

    JdbcDescriptor(final Rdbms rdbms,
                   final Specific specific) {
        this.rdbms = rdbms;
        this.specific = specific;
    }

    static final Map<Rdbms,JdbcDescriptor> DESCRIPTORS;

    static  {
        Map<Rdbms,JdbcDescriptor> descriptors = new HashMap<>();
        for (JdbcDescriptor descriptor : JdbcDescriptor.values())
            descriptors.put(descriptor.rdbms, descriptor);
        DESCRIPTORS = unmodifiableMap(descriptors);
    }
}
