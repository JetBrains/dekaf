package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.inter.exceptions.DBConnectionException;
import org.jetbrains.dekaf.inter.intf.InterSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.jetbrains.dekaf.jdbc.impl.JdbcStuff.closeIt;



public class JdbcSession implements InterSession {

    @NotNull
    private final JdbcFacade facade;

    @NotNull
    private final Connection connection;

    private final ArrayList<JdbcSeance> seances = new ArrayList<>();

    private boolean closed = false;


    public JdbcSession(@NotNull final JdbcFacade facade,
                       @NotNull final Connection connection) {
        this.facade = facade;
        this.connection = connection;
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
    public @NotNull JdbcSeance openSeance() {
        JdbcSeance seance = new JdbcSeance(this);
        synchronized (seances) {
            seances.add(seance);
        }
        return seance;
    }

    @NotNull
    Connection getConnection() {
        if (closed) throw new IllegalStateException("Session is closed");
        return connection;
    }

    @Override
    public void close() {
        if (closed) return;
        closeAllSeances();
        closeIt(connection);
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
