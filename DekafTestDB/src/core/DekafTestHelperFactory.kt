package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.*


/**
 * @author Leonid Bushuev 
 */
object DekafTestHelperFactory : DBTestHelperFactory {

    val supportedRdbms = setOf<Rdbms>(
            H2db.RDBMS,
            Postgres.RDBMS,
            Oracle.RDBMS,
            Mssql.RDBMS,
            Sybase.RDBMS,
            Mysql.RDBMS,
            Sqlite.RDBMS
    )


    override fun supportRdbms(): Set<Rdbms> = supportedRdbms


    override fun createTestHelperFor(facade: DBFacade): DBTestHelper =
            when (facade.rdbms()) {
                H2db.RDBMS     -> H2dbTestHelper(facade)
                Postgres.RDBMS -> PostgresTestHelper(facade)
                Oracle.RDBMS   -> OracleTestHelper(facade)
                Mssql.RDBMS    -> MssqlTestHelper(facade)
                Sybase.RDBMS   -> SybaseTestHelper(facade)
                Mysql.RDBMS    -> MysqlTestHelper(facade)
                Sqlite.RDBMS   -> SqliteTestHelper(facade)
                else           -> UnknownDBTestHelper(facade)
            }

}
