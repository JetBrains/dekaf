package org.jetbrains.dekaf

import org.jetbrains.dekaf.core.DBFacade
import org.jetbrains.dekaf.core.DBTestHelper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll


/**
 * @author Leonid Bushuev from JetBrains
 */
abstract class CommonIntegrationCase {

    companion object {

        @JvmStatic
        protected lateinit var DB: DBFacade

        @JvmStatic
        protected lateinit var TH: DBTestHelper

        @BeforeAll
        fun setupTestDB() {
            System.setProperty("java.awt.headless", "true")
            DB = TestDB.DB
            TH = TestDB.TH
        }

        @AfterAll
        fun disconnectFromTestDB() {
            if (DB != null) {
                TestDB.disconnect()
            }
        }
    }

}
