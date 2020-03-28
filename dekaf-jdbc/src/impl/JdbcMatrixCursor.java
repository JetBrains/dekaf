package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.exceptions.DBFetchingException;
import org.jetbrains.dekaf.inter.intf.InterMatrixCursor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import static org.jetbrains.dekaf.inter.utils.ArrayHacks.arrayClass;
import static org.jetbrains.dekaf.inter.utils.ArrayHacks.createArray;



public class JdbcMatrixCursor<B> extends JdbcBaseCursor implements InterMatrixCursor<B> {

    @NotNull
    private final Class<B> baseClass;

    @Nullable
    private JdbcValueGetter<? extends B>[] getters = null;

    JdbcMatrixCursor(final @NotNull JdbcSeance seance,
                     final @NotNull ResultSet rset,
                     final @NotNull Class<B> baseClass) {
        super(seance, rset);
        this.baseClass = baseClass;
    }

    @Override
    public void prepare(final @NotNull Class<? extends B> @NotNull [] cellClasses) {
        if (getters != null) throw new DBFetchingException("Cursor has been already prepared", seance.statementText);
        else if (isClosed()) throw new DBFetchingException("Cursor is closed", seance.statementText);

        int n = cellClasses.length;
        try {
            final ResultSetMetaData md = rset.getMetaData();
            final int               cc = md.getColumnCount();
            if (cc < n) {
                String msg = "Too few columns in the result set: expected " + n + " but actual " + cc;
                throw new DBFetchingException(msg, seance.statementText);
            }

            //noinspection unchecked
            getters = createArray(JdbcValueGetter.class, n);
            for (int i = 0; i < n; i++) {
                int jdbcType = md.getColumnType(i+1);
                JdbcValueGetter<? extends B> getter = JdbcValueGetters.of(jdbcType, cellClasses[i]);
                getters[i] = getter;
            }
        }
        catch (SQLException e) {
            throw new DBFetchingException("Failed to prepare cursor: "+e.getMessage(), e, seance.statementText);
        }
    }


    @Override @Nullable
    public B[][] fetchPortion() {
        if (end) return null;
        checkPrepared();

        int portionSize = seance.portionSize;

        B[][] array = null;
        int k = 0;

        try {
            while (k < portionSize) {
                boolean ok = rset.next();
                if (!ok) break;
                B[] row = handleRow();
                if (array == null) array = createArray(arrayClass(row), portionSize);
                array[k++] = row;
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
            B[][] array1 = Arrays.copyOf(array, k);
            return array1;
        }
        else {
            return array;
        }
    }

    @Nullable
    @Override
    public B[] fetchRow() {
        if (end) return null;
        checkPrepared();

        B[] row = null;

        try {
            boolean ok = rset.next();
            if (ok) {
                row = handleRow();
            }
        }
        catch (SQLException e) {
            throw new DBFetchingException("Failed to fetch a row from cursor: "+e.getMessage(), e, seance.statementText);
        }

        if (row == null) {
            end = true;
            close();
        }

        return row;
    }

    protected B[] handleRow() throws SQLException {
        final int n = getters.length;
        B[] row = createArray(baseClass, n);
        for (int i = 0; i < n; i++) {
            JdbcValueGetter<? extends B> getter = getters[i];
            //noinspection ConstantConditions
            B value = getter.getValue(rset, i+1);
            row[i] = value;
        }
        return row;
    }

    private void checkPrepared() {
        if (getters == null) {
            String msg = isClosed() ? "Cursor is closed" : "Cursor is not prepared";
            throw new DBFetchingException(msg, seance.statementText);
        }
    }

    @Override
    public void close() {
        getters = null;
        super.close();
    }
}
