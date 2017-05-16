package demo.kotlin

import org.jetbrains.dekaf.DekafMaster


object Step01 {

    @JvmStatic
    fun main(args : Array<String>) {

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