package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.InterCursor;
import org.jetbrains.dekaf.inter.InterSeance;
import org.jetbrains.dekaf.inter.InterTask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



final class JdbcSeance implements InterSeance {

    @NotNull
    private final JdbcSession session;

    @Nullable
    private InterTask task;

    @Nullable
    private PreparedStatement statement;

    @Nullable
    private ResultSet primaryResultSet;

    private int affectedRows;


    JdbcSeance(final @NotNull JdbcSession session) {
        this.session = session;
    }


    @Override
    public void prepare(final InterTask task) {
        if (task != null) throw new IllegalStateException("The seance is already prepared");
        this.task = task;
        String text = task.text;
        try {
            switch (task.kind) {
                case TASK_COMMAND:
                case TASK_QUERY:
                    statement = session.connection.prepareStatement(text);
                    break;
                case TASK_ROUTINE:
                    statement = session.connection.prepareCall(text);
                    break;
                case TASK_JDBC_METADATA:
                    statement = null;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown task kind: " + task.kind);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void assignParameters(final Object[] parameters) {

    }

    @Override
    public void execute() {
        try {
            switch (task.kind) {
                case TASK_COMMAND:
                case TASK_ROUTINE:
                    affectedRows = statement.executeUpdate();
                    break;
                case TASK_QUERY:
                    primaryResultSet = statement.executeQuery();
                    // TODO close the result set if it contains no data
                    break;
                case TASK_JDBC_METADATA:
                    // TODO implement
                    break;
                default:
                    throw new IllegalArgumentException("Unknown task kind: " + task.kind);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull InterCursor openCursor(final byte parameter) {
        return null;
    }

    @Override
    public void close() {
        if (primaryResultSet != null) closePrimaryResultSet();
        if (statement != null) closeStatement();
        task = null;
    }

    private void closeStatement() {
        try {
            statement.close();
        }
        catch (SQLException e) {
            // TODO log it somehow
        }
        finally {
            statement = null;
        }
    }

    private void closePrimaryResultSet() {
        try {
            primaryResultSet.close();
        }
        catch (SQLException e) {
            // TODO log it somehow
        }
        finally {
            primaryResultSet = null;
        }
    }
}
