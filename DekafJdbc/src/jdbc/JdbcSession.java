package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.UnknownDBException;
import org.jetbrains.dekaf.inter.InterSession;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.jetbrains.dekaf.util.Objects.castTo;



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
    private boolean closed;


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
    public synchronized @NotNull JdbcSeance openSeance() {
        JdbcSeance seance = new JdbcSeance(this);
        seances.add(seance);
        return seance;
    }

    @Override
    public int ping() {
        final long time1 = System.currentTimeMillis();

        try {
            specific.ping(connection);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        final long time2 = System.currentTimeMillis();
        return (int) (time2 - time1);
    }

    synchronized void seanceJustClosed(@NotNull JdbcSeance seance) {
        seances.remove(seance);
    }

    @Override
    public synchronized void close() {
        if (!seances.isEmpty()) {
            JdbcSeance[] seancesToClose = this.seances.toArray(new JdbcSeance[0]);
            for (int i = seancesToClose.length - 1; i >= 0; i--) {
                seancesToClose[i].close();  // TODO wrap by try/catch?
            }
        }
        if (inTransaction) {
            rollback();
        }
        JdbcUtil.close(connection);
        closed = true;
        facade.sessionClosed(this);
    }


    /// OTHER \\\


    @Override
    public <I> @Nullable I getSpecificService(@NotNull final Class<I> serviceClass,
                                              @NotNull final String serviceName)
            throws ClassCastException
    {
        switch (serviceName) {
            case Names.INTERMEDIATE_SERVICE: return castTo(serviceClass, this);
            case Names.JDBC_CONNECTION: return castTo(serviceClass, connection);
            case Names.JDBC_METADATA: return castTo(serviceClass, getConnectionMetadata());
            default: throw new IllegalArgumentException("JdbcSeance has no "+serviceName);
        }
    }

    private DatabaseMetaData getConnectionMetadata() {
        try {
            return connection.getMetaData();
        }
        catch (SQLException sqle) {
            throw new UnknownDBException(sqle, "Connection.getMetaData()");
        }
    }
}
