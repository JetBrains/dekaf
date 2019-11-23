package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.inter.exceptions.DBConnectionException;
import org.jetbrains.dekaf.inter.intf.InterSession;

import java.sql.Connection;
import java.sql.SQLException;



public class JdbcSession implements InterSession {

    @NotNull
    private final JdbcFacade facade;

    @NotNull
    private final Connection connection;

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
    public void close() {
        if (closed) return;
        try {
            connection.close();
        }
        catch (Exception e) {
            // panic!
        }
        finally {
            closed = true;
            facade.imClosed(this);
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
    
}
