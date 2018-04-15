package demo.kotlin

import org.jetbrains.dekaf.DekafMaster


/**
 * Step 01: Connect to a database, ping and disconnect.
 */
object Step01 {

    @JvmStatic
    fun main(args : Array<String>) {

        System.out.println(this.javaClass.simpleName)

        // Obtain a database facade
        val facade = DekafMaster.provider.provide(connectionString)

        // Connect to the database
        facade.connect()

        // Ping
        val respond = facade.ping()
        System.out.println("Ping respond in $respond ms.")

        // Disconnect
        facade.disconnect()

    }
    
}