package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.inter.intf.InterServiceFactory;

import java.sql.ResultSet;



public class JdbcServiceFactory implements InterServiceFactory {

    @Override
    @NotNull
    public JdbcFacade createFacade() {
        return new JdbcFacade(this);
    }

    @NotNull
    public JdbcSession createSession(final @NotNull JdbcFacade facade) {
        return new JdbcSession(facade);
    }

    @NotNull
    public JdbcSeance createSeance(final @NotNull JdbcSession session) {
        return new JdbcSeance(session);
    }

    @NotNull
    public <B> JdbcMatrixCursor<B> createMatrixCursor(final @NotNull JdbcSeance seance,
                                                      final @NotNull ResultSet rset,
                                                      final @NotNull Class<? extends B> baseClass) {
        return new JdbcMatrixCursor<>(seance, rset, baseClass);
    }

    @NotNull
    public <C> JdbcColumnCursor<C> createColumnCursor(final @NotNull JdbcSeance seance,
                                                      final @NotNull ResultSet rset,
                                                      final @NotNull Class<C> cellClass) {
        return new JdbcColumnCursor<>(seance, rset, cellClass);
    }

    @NotNull
    public JdbcIntsCursor createIntsCursor(final @NotNull JdbcSeance seance,
                                           final @NotNull ResultSet rset) {
        return new JdbcIntsCursor(seance, rset);
    }

    @NotNull
    public JdbcLongsCursor createLongsCursor(final @NotNull JdbcSeance seance,
                                             final @NotNull ResultSet rset) {
        return new JdbcLongsCursor(seance, rset);
    }

}
