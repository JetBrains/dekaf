package demo.kotlin

import org.jetbrains.dekaf.DekafMaster


/**
 * Step 01: Run a SQL command.
 */
object Step03 {

    @JvmStatic
    fun main(args : Array<String>) {

        // Obtain a database facade
        val facade = DekafMaster.provider.provide(connectionString)

        // Connect to the database
        facade.connect()

        // Run a simple DDL command
        val createTable = "create table my_table (id int, name varchar(40))"
        facade.inSessionDo {
            session -> session.command(createTable).run()
        }

        // Run a simple command with parameters
        val insert1 = "insert into my_table values (?,?),(?,?)"
        facade.inTransactionDo { transaction ->
            transaction.command(insert1)
                       .withParams(1, "Anna", 2, "Boris")
                       .run()
        }

        // Run a command with different parameters several time
        val insert2 = "insert into my_table values (?,?)"
        facade.inTransactionDo { transaction ->
            transaction.command(insert2)
                       .withParams(3, "Vlad").run()
                       .withParams(4, "Dana").run()
                       .withParams(5, "Emma").run()
        }

        // Run a command and get the number of affected rows
        val update = "update my_table set id = 100 + id"
        val affectedRows =
                facade.inTransaction { transaction ->
                    transaction.command(update)
                               .run()
                               .affectedRowsCount
                }
        System.out.println("Updated $affectedRows rows.")

        // Disconnect
        facade.disconnect()

    }
    
}