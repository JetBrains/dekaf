package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBFetchingException;
import org.jetbrains.dekaf.exceptions.DBParameterSettingException;
import org.jetbrains.dekaf.inter.InterLayout;
import org.jetbrains.dekaf.inter.InterSeance;
import org.jetbrains.dekaf.inter.InterTask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



final class JdbcSeance implements InterSeance {

    @NotNull
    private final JdbcSession session;

    @NotNull
    private final List<JdbcCursor> cursors = new ArrayList<>();

    @Nullable
    private InterTask task;

    @Nullable
    private PreparedStatement statement;

    private int affectedRows;
    private boolean returnedResultSet;



    JdbcSeance(final @NotNull JdbcSession session) {
        this.session = session;
    }


    @Override
    public synchronized void prepare(final InterTask task) {
        if (this.task != null) throw new IllegalStateException("The seance is already prepared");
        clearExecutionState();
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
    public synchronized void assignParameters(final Object[] parameters) {
        if (parameters == null) return;
        if (statement == null) throw new DBParameterSettingException("Statement is not prepared yet");

        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            try {
                JdbcParametersHandler.assignParameter(statement, i + 1, param);
            }
            catch (SQLException e) {
                String message = "Cannot assign parameter " + (i + 1) + ": " + e.getMessage();
                throw new DBParameterSettingException(message, e, getStatementText());
            }
        }
    }

    @Override
    public synchronized void execute() {
        clearExecutionState();

        if (task == null) throw new IllegalStateException("Task is not prepared");
        try {
            switch (task.kind) {
                case TASK_COMMAND:
                case TASK_ROUTINE:
                    if (statement == null) throw new IllegalStateException("JDBC statement for a routine is not prepared");
                    affectedRows = statement.executeUpdate();
                    break;
                case TASK_QUERY:
                    if (statement == null) throw new IllegalStateException("JDBC statement for a query is not prepared");
                    returnedResultSet = statement.execute();
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
    public synchronized int getAffectedRowsCount() {
        return affectedRows;
    }

    @Override
    public synchronized @Nullable JdbcCursor openCursor(final byte parameter,
                                                        final @NotNull InterLayout layout) {
        return parameter == 0
                ? openReturnedCursor(layout)
                : openParameterCursor(parameter, layout);
    }

    private @Nullable JdbcCursor openReturnedCursor(final @NotNull InterLayout layout) {
        if (returnedResultSet && statement != null) {
            ResultSet rset;
            try {
                rset = statement.getResultSet();
            }
            catch (SQLException e) {
                String msg = "Failed to obtain JDBC result set: " + e.getMessage();
                throw new DBFetchingException(msg, e, getStatementText());
            }
            JdbcCursor cursor = new JdbcCursor(this, layout, rset);
            cursors.add(cursor);
            return cursor;
        }
        else {
            return null;
        }
    }

    private @Nullable JdbcCursor openParameterCursor(final byte parameter,
                                                     final @NotNull InterLayout layout) {
        return null; // TODO
    }

    @Override
    public synchronized void close() {
        closeCursors();
        if (statement != null) closeStatement();
        task = null;
    }

    private void clearExecutionState() {
        closeCursors();
        returnedResultSet = true;
        affectedRows = 0;
    }

    private void closeStatement() {
        JdbcUtil.close(statement);
        statement = null;
    }

    private  void closeCursors() {
        JdbcCursor[] cursorsToClose = this.cursors.toArray(new JdbcCursor[0]);
        cursors.clear();
        for (int i = cursorsToClose.length-1; i >= 0; i--) {
            cursorsToClose[i].close();
        }
    }

    synchronized void cursorClosed(JdbcCursor cursor) {
        cursors.remove(cursor);
    }

    @Nullable
    String getStatementText() {
        return task != null ? task.text : null;
    }

}
