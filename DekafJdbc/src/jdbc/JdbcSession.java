package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.inter.InterSeance;
import org.jetbrains.dekaf.inter.InterSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;



final class JdbcSession implements InterSession {

    @NotNull
    final JdbcFacade facade;

    @NotNull
    final Connection connection;

    @NotNull
    final Specific specific;

    @NotNull
    private final ArrayList<JdbcSeance> seances = new ArrayList<>();

    private boolean inTransaction;


    JdbcSession(final @NotNull JdbcFacade facade, final @NotNull Connection connection) {
        this.facade = facade;
        this.connection = connection;
        this.specific = facade.specific;
    }


    /// TRANSACTIONS \\\

    @Override
    public void begin() {
        try {
            specific.transactionBegin(connection);
            inTransaction = true;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            specific.transactionCommit(connection);
            inTransaction = false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            specific.transactionRollback(connection);
            inTransaction = false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isInTransaction() {
        return inTransaction;
    }


    /// SEANCES \\\

    @Override
    public synchronized @NotNull InterSeance openSeance() {
        JdbcSeance seance = new JdbcSeance(this);
        seances.add(seance);
        return seance;
    }

    synchronized void seanceJustClosed(@NotNull JdbcSeance seance) {
        seances.remove(seance);
    }

    @Override
    public synchronized void close() {
        if (seances.isEmpty()) return;
        JdbcSeance[] seancesToClose = this.seances.toArray(new JdbcSeance[0]);
        for (int i = seancesToClose.length-1; i >= 0; i--) {
            seancesToClose[i].close();  // TODO wrap by try/catch?
        }
    }
}
