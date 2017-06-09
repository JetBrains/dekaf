package demo.java;

import org.jetbrains.dekaf.DekafMaster;
import org.jetbrains.dekaf.core.DBFacade;
import org.jetbrains.dekaf.core.QueryResultLayout;

import static org.jetbrains.dekaf.core.QueryLayouts.layoutArrayOfInt;
import static org.jetbrains.dekaf.core.QueryLayouts.layoutArrayOfLong;



/**
 * Step 05: SQL query that returns a column of integers
 */
public final class Step05 {

    static final String queryText =
            "select id                            \n" +
            "from table                           \n" +
            "     (                               \n" +
            "        id int = (11,22,33,44,55,66) \n" +
            "     )                               \n";


    public static void main(String[] args) {

        System.out.println(Step05.class.getSimpleName());

        // Obtain a database facade and connect to the database
        DBFacade facade = DekafMaster.provider.provide(Consts.connectionString);
        facade.connect();

        // Query an array of int
        QueryResultLayout<int[]> layout1 = layoutArrayOfInt();
        facade.inTransactionDo(tran -> {
            int[] identifiers = tran.query(queryText, layout1).run();
            for (int i = 0; i < identifiers.length; i++) System.out.print(identifiers[i] + "  ");
            System.out.println("");
        });

        // Query an array of long
        QueryResultLayout<long[]> layout2 = layoutArrayOfLong();
        facade.inTransactionDo(tran -> {
            long[] identifiers = tran.query(queryText, layout2).run();
            for (int i = 0; i < identifiers.length; i++) System.out.print(identifiers[i] + "  ");
            System.out.println("");
        });

        // Disconnect
        facade.disconnect();

    }


}
