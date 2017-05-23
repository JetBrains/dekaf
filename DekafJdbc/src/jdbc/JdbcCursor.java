package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBFetchingException;
import org.jetbrains.dekaf.inter.InterCursor;
import org.jetbrains.dekaf.inter.InterLayout;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;



class JdbcCursor implements InterCursor {

    private final @NotNull JdbcSeance seance;

    private final @NotNull InterLayout layout;

    private @Nullable ResultSet rset;

    private boolean active;

    private int portionSize = 100;





    JdbcCursor(final @NotNull JdbcSeance seance,
               final @NotNull InterLayout layout,
               final @Nullable ResultSet rset) {
        this.seance = seance;
        this.layout = layout;
        this.rset = rset;
        this.active = rset != null;
    }

    @Override
    public synchronized @Nullable Serializable retrievePortion() {
        if (!active || rset == null) return null;
        switch (layout.resultKind) {
            case RES_EXISTENCE: return retrieveExistence();
            case RES_ONE_ROW: return retrieveOneRow();
            case RES_TABLE: return retrieveTable();
            default: throw new IllegalStateException("Unknown how to retrieve a " + layout.resultKind);
        }
    }

    private Boolean retrieveExistence() {
        boolean existence = moveNext();
        close();
        return existence;
    }

    private Serializable retrieveOneRow() {
        Serializable row = fetchRow();
        close();
        return row;
    }

    private Serializable retrieveTable() {
        return null; // TODO
    }

    @Nullable
    private Serializable fetchRow() {
        boolean ok = moveNext();
        if (ok) {
            switch (layout.rowKind) {
                case ROW_ONE_VALUE: return fetchOneValue();
                case ROW_CORTEGE: return fetchCortege();
                case ROW_NONE: return null;
                default: throw new IllegalStateException("Unknown how to fetch a " + layout.rowKind);
            }
        }
        else {
            return null;
        }
    }

    @Nullable
    private Serializable fetchOneValue() {
        // TODO
        try {
            return (Serializable) rset.getObject(1);
        }
        catch (SQLException e) {
            throw new DBFetchingException(e, seance.getStatementText());
        }
    }

    @Nullable
    private Serializable[] fetchCortege() {
        return null; // TODO
    }

    private boolean moveNext() {
        try {
            boolean ok = rset.next();
            if (!ok) close();
            return ok;
        }
        catch (SQLException e) {
            close();
            String msg = "Failed to determine whether the result set has rows: " + e.getMessage();
            throw new DBFetchingException(msg, e, seance.getStatementText());
        }
    }

    @Override
    public synchronized void close() {
        if (active) {
            JdbcUtil.close(rset);
            rset = null;
            active = false;
        }
    }
    
}
