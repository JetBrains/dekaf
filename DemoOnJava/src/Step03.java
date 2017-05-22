package demo.java;

import org.jetbrains.dekaf.DekafMaster;
import org.jetbrains.dekaf.core.DBFacade;



/**
 * Step 01: Run a SQL command.
 */
public final class Step03 {

    public static void main(String[] args) {

        System.out.println(Step03.class.getSimpleName());

        // Obtain a database facade
        DBFacade facade = DekafMaster.provider.provide(Consts.connectionString);

        // Connect to the database
        facade.connect();

        // Run a simple DDL command
        String createTable = "create table my_table (id int, name varchar(40))";
        facade.inSessionDo(session -> {
            session.command(createTable).run();
        });

        // Run a simple command with parameters
        String insert1 = "insert into my_table values (?,?),(?,?)";
        facade.inTransactionDo(transaction ->
                                       transaction.command(insert1)
                                                     .withParams(1,"Anna",2,"Boris").run());

        // Run a command with different parameters several time
        String insert2 = "insert into my_table values (?,?)";
        facade.inTransactionDo(transaction ->
                                       transaction.command(insert2)
                                                     .withParams(3,"Vlad").run()
                                                     .withParams(4,"Dana").run()
                                                     .withParams(5,"Emma").run());

        // Run a command and get the number of affected rows
        String update = "update my_table set id = 100 + id";
        int affectedRows =
            facade.inTransaction(transaction ->
                                         transaction.command(update)
                                                    .run()
                                                    .getAffectedRowsCount());
        System.out.printf("Updated %d rows.\n", affectedRows);

        // Disconnect
        facade.disconnect();

    }

}
