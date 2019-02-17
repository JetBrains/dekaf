package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.H2db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import java.util.function.Function;



public class JdbcUnitTestCase {


    public static final JdbcProvider provider;
    public static final JdbcFacade hmFacade;


    static {
        provider = new JdbcProvider();
        hmFacade = JdbcUnitTestCase.provider.createFacade(H2db.RDBMS);
        hmFacade.setUp(H2mem.settings);
    }



    public static void hmPerformCommand(final @NotNull String command) {
        Connection connection = hmFacade.obtainConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                statement.execute(command);
            }
            finally {
                statement.close();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            closeConnection(connection);
        }
    }

    public static <R> R hmInSession(Function<JdbcSession,R> block) {
        hmFacade.activate();
        JdbcSession session = hmFacade.openSession();
        try {
            return block.apply(session);
        }
        finally {
            session.close();
        }
    }

    public static void hmInSessionDo(Consumer<JdbcSession> block) {
        hmFacade.activate();
        JdbcSession session = hmFacade.openSession();
        try {
            block.accept(session);
        }
        finally {
            session.close();
        }
    }

    private static void closeConnection(final Connection connection) {
        try {
            connection.close();
        }
        catch (Exception e) {
            System.err.println("Failed to close JDBC connection: "+e.getMessage());
        }

    }

    private static void closeStatement(final Statement statement) {
        try {
            statement.close();
        }
        catch (Exception e) {
            System.err.println("Failed to close JDBC statement: "+e.getMessage());
        }
    }
}
