package demo.java;

import org.jetbrains.dekaf.DekafMaster;
import org.jetbrains.dekaf.core.DBFacade;
import org.jetbrains.dekaf.core.DBLeasedSession;



/**
 * Step 02: Sessions and transactions
 */
public final class Step02 {

    public static void main(String[] args) {

        // Obtain a database facade
        DBFacade facade = DekafMaster.provider.provide(Consts.connectionString);

        // Connect to the database
        facade.connect();

        // Do something in transaction
        facade.inTransactionDo(tran -> {

            // do something inside this tran

        });

        // Do something with sessions
        facade.inSessionDo(session -> {

            // do something with session

            // do something in transaction of this session
            session.inTransactionDo(tran -> {

                // do something inside this tran

            });

        });

        // Use leased session
        DBLeasedSession session = facade.leaseSession();

        // Do something with this leased session
        session.ping();

        // Don't forget to close the session
        session.close();


        // Disconnect
        facade.disconnect();

    }

}
