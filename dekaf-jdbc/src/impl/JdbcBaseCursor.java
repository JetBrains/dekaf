package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.inter.intf.InterCursor;

import java.sql.ResultSet;



public abstract class JdbcBaseCursor implements InterCursor {

    @NotNull
    protected final JdbcSeance seance;

    @NotNull
    protected final ResultSet rset;

    protected boolean end = false;
    private boolean closed = false;


    protected JdbcBaseCursor(@NotNull final JdbcSeance seance, @NotNull final ResultSet rset) {
        this.seance = seance;
        this.rset = rset;
    }

    @Override
    public void close() {
        JdbcUtil.close(rset);
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

}
