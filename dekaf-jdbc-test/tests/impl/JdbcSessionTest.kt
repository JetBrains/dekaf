package org.jetbrains.dekaf.jdbcTest.impl

import lb.yaka.expectations.equalsTo
import lb.yaka.gears.expect
import org.junit.jupiter.api.Test


class JdbcSessionTest : H2ConnectedTest() {

    @Test
    fun tran_beginAndCommit() {
        expect that session.isInTransaction equalsTo false

        session.beginTransaction()

        expect that session.isInTransaction equalsTo true
        expect that session.isAutocommit equalsTo false

        session.commit()

        expect that session.isInTransaction equalsTo false
    }

    @Test
    fun tran_beginAndRollback() {
        expect that session.isInTransaction equalsTo false

        session.beginTransaction()

        expect that session.isInTransaction equalsTo true
        expect that session.isAutocommit equalsTo false

        session.rollback()

        expect that session.isInTransaction equalsTo false
    }


    @Test
    fun autocommit_byDefault() {
        val ses = facade.openSession()
        try {
            expect that ses.isAutocommit equalsTo true
        }
        finally {
            ses.close()
        }
    }

    @Test
    fun autocommit_afterTransaction() {
        val ses = facade.openSession()
        try {
            ses.beginTransaction()
            ses.commit()
            val seance = ses.openSeance()
            try {
                expect that ses.isAutocommit equalsTo true
            }
            finally {
                seance.close()
            }
        }
        finally {
            ses.close()
        }
    }


}