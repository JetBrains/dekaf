package org.jetbrains.dekaf

import org.jetbrains.dekaf.TestEnvironment.obtainConnectionString
import org.jetbrains.dekaf.core.DBFacade
import org.jetbrains.dekaf.core.DBTestHelper
import org.jetbrains.dekaf.core.DekafTestHelperFactory


/**
 * @author Leonid Bushuev from JetBrains
 */
object TestDB {

    /**
     * Test database.
     */
    @JvmStatic
    var DB: DBFacade

    /**
     * Test database helper.
     */
    @JvmStatic
    var TH: DBTestHelper

    init {
        DB = prepareDB()
        TH = prepareTH(DB)
    }

    fun reinitDB() {
        DB.disconnect()
        DB = prepareDB()
        TH = prepareTH(DB)
    }

    private fun prepareDB(): DBFacade {
        val connectionString = obtainConnectionString()
        val facade = DekafMaster.provider.provide(connectionString)
        return facade
    }

    @JvmStatic
    fun connect() {
        DB.connect()
    }

    @JvmStatic
    fun disconnect() {
        DB.disconnect()
    }

    private fun prepareTH(db: DBFacade): DBTestHelper =
            DekafTestHelperFactory.createTestHelperFor(db)
}
