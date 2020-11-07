package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.exceptions.DBFetchingException;
import org.jetbrains.dekaf.inter.intf.InterColumnCursor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import static org.jetbrains.dekaf.inter.utils.ArrayHacks.createArray;



public class JdbcColumnCursor<C> extends JdbcBaseCursor implements InterColumnCursor<C> {

    @NotNull
    private final Class<C> cellClass;

    @Nullable
    private JdbcValueGetter<C> getter = null;

    protected JdbcColumnCursor(final @NotNull JdbcSeance seance,
                               final @NotNull ResultSet rset,
                               final @NotNull Class<C> cellClass) {
        super(seance, rset);
        this.cellClass = cellClass;
    }

    @Override
    public void prepare() {
        if (getter != null) throw new DBFetchingException("Cursor has been already prepared", seance.statementText);
        else if (isClosed()) throw new DBFetchingException("Cursor is closed", seance.statementText);

        try {
            final ResultSetMetaData md = rset.getMetaData();
            int jdbcType = md.getColumnType(1);
            getter = JdbcValueGetters.of(jdbcType, cellClass);
        }
        catch (SQLException e) {
            throw new DBFetchingException("Failed to prepare cursor: "+e.getMessage(), e, seance.statementText);
        }
    }


    @Override @Nullable
    public C[] fetchPortion() {
        if (end) return null;
        checkPrepared();

        int portionSize = seance.portionSize;

        C[] array = null;
        int k = 0;

        try {
            while (k < portionSize) {
                boolean ok = rset.next();
                if (!ok) break;
                C value = getter.getValue(rset, 1);
                if (array == null) array = createArray(cellClass, portionSize);
                array[k++] = value;
            }
        }
        catch (SQLException e) {
            throw new DBFetchingException("Failed to fetch portion from cursor: "+e.getMessage(), e, seance.statementText);
        }

        if (k == 0) {
            end = true;
            close();
            return null;
        }
        else if (k < portionSize) {
            end = true;
            close();
            //noinspection UnnecessaryLocalVariable
            C[] array1 = Arrays.copyOf(array, k);
            return array1;
        }
        else {
            return array;
        }
    }

    @Nullable
    @Override
    public C[] fetchRow() {
        if (end) return null;
        checkPrepared();

        try {
            boolean ok = rset.next();
            if (ok) {
                C value = getter.getValue(rset, 1);
                C[] result = createArray(cellClass, 1);
                result[0] = value;
                return result;
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            throw new DBFetchingException("Failed to fetch a row from cursor: "+e.getMessage(), e, seance.statementText);
        }
    }

    private void checkPrepared() {
        if (getter == null) {
            String msg = isClosed() ? "Cursor is closed" : "Cursor is not prepared";
            throw new DBFetchingException(msg, seance.statementText);
        }
    }

    @Override
    public void close() {
        getter = null;
        super.close();
    }
}
