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

import static org.jetbrains.dekaf.inter.common.StatementCategory.stmtQuery;
import static org.jetbrains.dekaf.inter.common.StatementCategory.stmtSimple;
import static org.jetbrains.dekaf.jdbc.impl.JdbcParametersHandler.assignNull;
import static org.jetbrains.dekaf.jdbc.impl.JdbcParametersHandler.assignValueByItsType;
import static org.jetbrains.dekaf.jdbc.impl.JdbcStuff.closeIt;



public class JdbcSeance implements InterSeance {

    @NotNull
    protected final JdbcSession session;

    protected PreparedStatement stmt = null;

    @NotNull
    protected StatementCategory category = stmtSimple;

    @Nullable
    protected String statementText = null;

    @Nullable
    protected ParamDef[] paramsDefs = null;

    protected boolean hasInParams  = false;
    protected boolean hasOutParams = false;
    protected int     paramCount   = 0;

    protected int affectedRows = 0;

    @Nullable
    protected ResultSet rset;

    private boolean closed = false;


    public JdbcSeance(final @NotNull JdbcSession session) {
        this.session = session;
    }


    @Override
    public void prepare(final @NotNull String statementText,
                        final @NotNull StatementCategory category,
                        final @Nullable ParamDef[] paramDefs) {
        if (closed) throw new IllegalStateException("Seance is closed");

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
    public void execute(@Nullable final Iterable<?> paramValues) {
        if (closed) throw new IllegalStateException("Seance is closed");
        if (stmt == null) throw new IllegalStateException("Seance is not prepared");

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

    @Override
    public void close() {
        if (rset != null) {
            closeIt(rset);
            rset = null;
        }
        if (stmt != null) {
            closeIt(stmt);
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
    
}
