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


    override fun createTestHelperFor(db: DBFacade): DBTestHelper =
            when (db.rdbms()) {
                H2db.RDBMS     -> H2dbTestHelper(db)
                Postgres.RDBMS -> PostgresTestHelper(db)
                Oracle.RDBMS   -> OracleTestHelper(db)
                Mssql.RDBMS    -> MssqlTestHelper(db)
                Sybase.RDBMS   -> SybaseTestHelper(db)
                Mysql.RDBMS    -> MysqlTestHelper(db)
                Sqlite.RDBMS   -> SqliteTestHelper(db)
                else           -> UnknownDBTestHelper(db)
            }

}
