package demo.java;

import org.jetbrains.dekaf.DekafMaster;
import org.jetbrains.dekaf.core.DBFacade;



/**
 * Step 01: Connect to a database, ping and disconnect.
 */
public final class Step01 {

    public static void main(String[] args) {

        // Obtain a database facade
        DBFacade facade = DekafMaster.provider.provide(Consts.connectionString);

        // Connect to the database
        facade.connect();

        // Ping
        int respond = facade.ping();
        System.out.printf("Ping respond in %d ms.\n", respond);

        // Disconnect
        facade.disconnect();

    }

}
