package demo.java;

import org.jetbrains.dekaf.DekafMaster;
import org.jetbrains.dekaf.core.DBFacade;
import org.jetbrains.dekaf.core.QueryResultLayout;

import java.util.List;

import static org.jetbrains.dekaf.core.QueryLayouts.listOf;
import static org.jetbrains.dekaf.core.QueryLayouts.structOf;



/**
 * Step 04: SQL query a table of corteges
 */
public final class Step04 {

    static final String queryText =
            "select *                                                          \n" +
            "from table                                                        \n" +
            "     (                                                            \n" +
            "        id      int         = (11,22,33,44),                      \n" +
            "        name    varchar(20) = ('Masha','Dasha','Glasha','Pasha'), \n" +
            "        sex     char(1)     = ('F','F','F','M'),                  \n" +
            "        married boolean     = (0,1,1,0)                           \n" +
            "     )                                                            \n";

    static final class Person {
        int     id;
        String  name;
        char    sex;
        boolean married;

        public String toString() { return id + "/" + name + "/" + sex + "/" + married; }
    }

    public static void main(String[] args) {

        System.out.println(Step04.class.getSimpleName());

        // Obtain a database facade and connect to the database
        DBFacade facade = DekafMaster.provider.provide(Consts.connectionString);
        facade.connect();

        // Query a list of Person
        QueryResultLayout<List<Person>> layout = listOf(structOf(Person.class));
        facade.inTransactionDo(tran -> {
            List<Person> people = tran.query(queryText, layout).run();
            for (Person person : people) System.out.println(person);
        });

        // Disconnect
        facade.disconnect();

    }


}
