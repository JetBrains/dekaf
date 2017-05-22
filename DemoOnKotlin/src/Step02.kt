package demo.kotlin

import org.jetbrains.dekaf.DekafMaster
import org.jetbrains.dekaf.core.DBTransaction


/**
 * Step 01: Use sessions and transactions.
 */
object Step02 {

    @JvmStatic
    fun main(args : Array<String>) {

        System.out.println(this.javaClass.simpleName)

        // Obtain a database facade
        val facade = DekafMaster.provider.provide(connectionString)

        // Connect to the database
        facade.connect()


        // Do something in transaction
        facade.inTransactionDo { tran: DBTransaction ->

            // do something inside this tran

        }


        // Do something with sessions
        facade.inSessionDo { session ->

            // do something with session

            // do something in transaction of this session
            session.inTransactionDo { tran ->

                // do something inside this tran

            }

        }


        // Use leased session
        val session = facade.leaseSession()

        // Do something with this leased session
        session.ping()

        // Don't forget to close the session
        session.close()


        // Disconnect
        facade.disconnect()

    }
    
}