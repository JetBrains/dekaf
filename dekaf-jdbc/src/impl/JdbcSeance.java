package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.common.ParamDef;
import org.jetbrains.dekaf.inter.common.ParamType;
import org.jetbrains.dekaf.inter.common.StatementCategory;
import org.jetbrains.dekaf.inter.exceptions.DBPreparingException;
import org.jetbrains.dekaf.inter.exceptions.UnexpectedDBException;
import org.jetbrains.dekaf.inter.intf.InterSeance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static org.jetbrains.dekaf.inter.common.StatementCategory.stmtQuery;
import static org.jetbrains.dekaf.inter.common.StatementCategory.stmtSimple;
import static org.jetbrains.dekaf.jdbc.impl.JdbcParametersHandler.assignNull;
import static org.jetbrains.dekaf.jdbc.impl.JdbcParametersHandler.assignValueByItsType;



public class JdbcSeance implements InterSeance {

    @NotNull
    protected final JdbcSession session;

    protected PreparedStatement stmt = null;

    @NotNull
    protected StatementCategory category = stmtSimple;

    @Nullable
    protected String statementText = null;

    protected int portionSize = 1000;

    @Nullable
    protected ParamDef[] paramsDefs = null;

    protected boolean hasInParams  = false;
    protected boolean hasOutParams = false;
    protected int     paramCount   = 0;

    protected int affectedRows = 0;

    @Nullable
    protected ResultSet rset;

    private boolean closed = false;

    private final Collection<JdbcBaseCursor> cursors = new ArrayList<>();


    protected JdbcSeance(final @NotNull JdbcSession session) {
        this.session = session;
    }

    @Override
    public void setPortionSize(final int portionSize) {
        if (portionSize < 1) throw new IllegalArgumentException("Wrong portion size: " + portionSize);
        this.portionSize = portionSize;
    }

    @Override
    public void prepare(final @NotNull String statementText,
                        final @NotNull StatementCategory category,
                        final /*@NotNull*/ ParamDef @Nullable [] paramDefs) {
        checkNotClosed();

        this.category = category;
        this.statementText = statementText;
        
        this.paramsDefs = paramDefs;
        if (paramDefs != null) {
            paramCount = paramDefs.length;
            for (int i = 0; i < paramCount; i++) {
                ParamDef p = paramDefs[i];
                assert p != null;
                hasInParams = hasInParams || p.direction.isIn;
                hasOutParams = hasOutParams || p.direction.isOut;
                if (hasInParams && hasOutParams) break;
            }
        }
        else {
            hasInParams = hasOutParams = false;
            paramCount = 0;
        }

        stmt = makeStatement(statementText);
    }

    @NotNull
    protected PreparedStatement makeStatement(final @NotNull String statementText) {
        Connection conn = session.getConnection();
        try {
            PreparedStatement stmt = prepareStatement(conn, statementText);
            setFetchSize(stmt);
            return stmt;
        }
        catch (SQLException sqle) {
            throw new DBPreparingException(sqle, statementText);
        }
    }

    protected PreparedStatement prepareStatement(final @NotNull Connection conn,
                                                 final @NotNull String statementText)
            throws SQLException {
        return hasOutParams
               ? conn.prepareCall(statementText)
               : conn.prepareStatement(statementText);
    }

    protected void setFetchSize(final @NotNull PreparedStatement stmt)
            throws SQLException {
        if (category == stmtQuery) stmt.setFetchSize(1000);
    }


    @Override
    public boolean isPrepared() {
        return stmt != null;
    }

    @Override
    public void execute(@Nullable final Iterable<?> paramValues) {
        checkPrepared();

        affectedRows = 0;
        rset = null;

        if (paramValues != null) assignParams(paramValues);

        try {
            switch (category) {
                case stmtSimple:
                    executeSimple();
                    break;
                case stmtUpdate:
                    executeUpdate();
                    break;
                case stmtQuery:
                    executeQuery();
                    break;
            }
        }
        catch (SQLException sqle) {
            throw new UnexpectedDBException(sqle, statementText);
        }
        catch (Exception e) {
            throw new UnexpectedDBException("Failed to execute the statement", e, statementText);
        }
    }

    protected void assignParams(final @NotNull Iterable<?> paramValues)
    {
        if (paramsDefs != null) assignTypedParams(paramValues);
        else assignSimpleParams(paramValues);
    }

    private void assignSimpleParams(final @NotNull Iterable<?> paramValues) {
        int index = 0;
        for (Object value : paramValues) {
            index++;
            assignParam(index, null, value);
        }
    }

    private void assignTypedParams(final @NotNull Iterable<?> paramValues) {
        //noinspection ConstantConditions
        int n = paramsDefs.length;
        int k = 0;
        for (Object value : paramValues) {
            ParamDef p = null;
            while (k < n) {
                p = paramsDefs[k++];
                assert p != null;
                if (p.direction.isIn) break;
            }
            if (p == null || k >= n) break;
            int index = k /* already incremented */;
            assignParam(index, p.type, value);
        }
    }

    protected void assignParam(final int index,
                               final @Nullable ParamType type,
                               final @Nullable Object value) {
        if (value == null)
            if (type == null) assignSimpleNull(index);
            else assignTypedNull(index, type);
        else
            if (type == null) assignSimpleParam(index, value);
            else assignTypedParam(index, type, value);
    }

    protected void assignSimpleParam(final int index,
                                     final @NotNull Object value) {
        assignValueByItsType(stmt, index, value);
    }

    protected void assignTypedParam(final int index,
                                    final @NotNull ParamType type,
                                    final @NotNull Object value) {
        // TODO use type
        assignValueByItsType(stmt, index, value);
    }

    protected void assignSimpleNull(final int index) {
        assignNull(stmt, index);
    }

    protected void assignTypedNull(final int index, final @NotNull ParamType type) {
        // TODO use type
        assignNull(stmt, index);
    }

    protected void executeSimple()
            throws SQLException
    {
        stmt.execute();
    }

    protected void executeUpdate()
            throws SQLException
    {
        affectedRows = stmt.executeUpdate();
    }

    protected void executeQuery()
            throws SQLException
    {
        rset = stmt.executeQuery();
    }

    public int getAffectedRows() {
        return affectedRows;
    }


    @Override @NotNull
    public <B> JdbcMatrixCursor<B> makeMatrixCursor(final int parameter, Class<? extends B> baseClass) {
        ResultSet rset = getResultSet(parameter);
        JdbcMatrixCursor<B> cursor =
                session.facade.factory.createMatrixCursor(this, rset, baseClass);
        cursors.add(cursor);
        return cursor;
    }

    @Override @NotNull
    public <C> JdbcColumnCursor<C> makeColumnCursor(final int parameter, @NotNull Class<C> cellClass) {
        ResultSet rset   = getResultSet(parameter);
        JdbcColumnCursor<C> cursor =
                session.facade.factory.createColumnCursor(this, rset, cellClass);
        cursors.add(cursor);
        return cursor;
    }

    @Override @NotNull
    public JdbcIntsCursor makeIntsCursor(final int parameter) {
        ResultSet rset   = getResultSet(parameter);
        JdbcIntsCursor cursor =
                session.facade.factory.createIntsCursor(this, rset);
        cursors.add(cursor);
        return cursor;
    }

    @Override @NotNull
    public JdbcLongsCursor makeLongsCursor(final int parameter) {
        ResultSet rset   = getResultSet(parameter);
        JdbcLongsCursor cursor =
                session.facade.factory.createLongsCursor(this, rset);
        cursors.add(cursor);
        return cursor;
    }

    @NotNull
    protected ResultSet getResultSet(final int parameter) {
        checkPrepared();
        if (parameter == 0) {
            if (rset != null) return rset;
            else throw new IllegalStateException("No primary result set");
        }
        else {
            throw new RuntimeException("Not implemented yet: getting result set from a parameter");
        }
    }


    @Override
    public void close() {
        for (JdbcBaseCursor cursor : cursors) {
            cursor.close();
            if (rset == cursor.rset) rset = null;
        }
        cursors.clear();

        if (rset != null) {
            JdbcUtil.close(rset);
            rset = null;
        }
        if (stmt != null) {
            JdbcUtil.close(stmt);
            stmt = null;
        }
        
        closed = true;
        session.seanceJustClosed(this);
        paramsDefs = null;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    protected void checkNotClosed() {
        if (closed) throw new IllegalStateException("Seance is closed");
    }

    protected void checkPrepared() {
        if (stmt == null) {
            String msg = closed ? "Seance is closed" : "Seance is not prepared";
            throw new IllegalStateException(msg);
        }
    }

}
