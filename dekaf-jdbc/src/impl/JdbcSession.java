package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.inter.exceptions.DBConnectionException;
import org.jetbrains.dekaf.inter.exceptions.DBTransactionException;
import org.jetbrains.dekaf.inter.exceptions.DBTransactionIsAlreadyStartedException;
import org.jetbrains.dekaf.inter.intf.InterSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;



public class JdbcSession implements InterSession {

    @NotNull
    private final JdbcFacade facade;

    @NotNull
    private final Connection connection;

    private final ArrayList<JdbcSeance> seances = new ArrayList<>();

    private boolean autoCommit = false;
    private boolean inTransaction = false;
    private boolean closed = false;


    public JdbcSession(@NotNull final JdbcFacade facade,
                       @NotNull final Connection connection) {
        this.facade = facade;
        this.connection = connection;
        updateAutocommitFlag();
    }

    private void updateAutocommitFlag() {
        try {
            this.autoCommit = connection.getAutoCommit();
        }
        catch (SQLException e) {
            throw new DBTransactionException("Failed to check auto-commit mode: " + e.getMessage(), e);
        }
    }

    private void setAutocommitMode(boolean ac) {
        try {
            connection.setAutoCommit(ac);
            autoCommit = ac;
        }
        catch (SQLException e) {
            String s1 = ac ? "ON" : "OFF";
            throw new DBTransactionException("Failed to turn mode " + s1 + ": " + e.getMessage(), e);
        }
    }


    @Override
    public void ping()
            throws DBConnectionException
    {
        try {
            boolean ok = connection.isValid(3 /* seconds */);
            if (!ok) throw new DBConnectionException("The connection is broken: connection.isValid() returned false");
        }
        catch (SQLException e) {
            throw new DBConnectionException("The connection is broken: " + e.getMessage(), e, null);
        }
    }


    @Override
    public void beginTransaction() {
        if (inTransaction) {
            throw new DBTransactionIsAlreadyStartedException();
        }
        if (autoCommit) {
            setAutocommitMode(false);
        }
        inTransaction = true;
    }

    @Override
    public void commit() {
        try {
            connection.commit();
            inTransaction = false;
        }
        catch (SQLException e) {
            throw new DBConnectionException("The connection is broken: " + e.getMessage(), e, null);
        }
    }

    @Override
    public void rollback() {
        try {
            connection.rollback();
        }
        catch (SQLException e) {
            // TODO log somehow! and panic!
        }
        inTransaction = false;
    }

    @Override
    public boolean isInTransaction() {
        return inTransaction;
    }

    public boolean isAutocommit() {
        return autoCommit;
    }

    @Override
    public @NotNull JdbcSeance openSeance() {
        if (!inTransaction && !autoCommit) setAutocommitMode(true);
        JdbcSeance seance = new JdbcSeance(this);
        synchronized (seances) {
            seances.add(seance);
        }
        return seance;
    }

    @NotNull @ApiStatus.Internal
    public Connection getConnection() {
        if (closed) throw new IllegalStateException("Session is closed");
        return connection;
    }

    @Override
    public void close() {
        if (closed) return;
        closeAllSeances();
        if (inTransaction) rollback();
        facade.releaseConnection(connection);
        closed = true;
        facade.sessionJustClosed(this);
    }

    void seanceJustClosed(final JdbcSeance seance) {
        synchronized (seances) {
            seances.remove(seance);
        }
    }

    public void closeAllSeances() {
        JdbcSeance[] seancesToClose;
        synchronized (seances) {
            seancesToClose = seances.toArray(new JdbcSeance[0]);
            seances.clear();
        }
        for (int i = seancesToClose.length - 1; i >= 0; i--) {
            seancesToClose[i].close();
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
    
}
