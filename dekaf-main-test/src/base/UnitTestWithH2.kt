package org.jetbrains.dekaf.mainTest.base

import org.jetbrains.dekaf.main.DbMaster
import org.jetbrains.dekaf.main.base.BaseFacade
import org.jetbrains.dekaf.mainTest.util.H2memSettings
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
interface UnitTestWithH2 : UnitTest {

    companion object {

        lateinit var dbFacade: BaseFacade

        @BeforeAll @JvmStatic
        fun initH2() {
            dbFacade = DbMaster.open(H2memSettings) as BaseFacade
            dbFacade.connect()
        }

        @AfterAll @JvmStatic
        fun closeH2() {
            dbFacade.disconnect()
        }

    }


    val dbf: BaseFacade
        get() = dbFacade


}