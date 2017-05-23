package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBFetchingException;
import org.jetbrains.dekaf.inter.InterCursor;
import org.jetbrains.dekaf.inter.InterLayout;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import static org.jetbrains.dekaf.inter.InterResultKind.RES_EXISTENCE;



class JdbcCursor implements InterCursor {

    private final @NotNull JdbcSeance seance;

    private final @NotNull InterLayout layout;

    private @Nullable ResultSet rset;

    private boolean active;

    private int portionSize = 100;

    private int columnCount = 0;
    private int fieldCount = 0;

    private int[] indexMapping;



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
        setupOfNecessary();
        switch (layout.resultKind) {
            case RES_EXISTENCE: return retrieveExistence();
            case RES_ONE_ROW: return retrieveOneRow();
            case RES_TABLE: return retrieveTable();
            default: throw new IllegalStateException("Unknown how to retrieve a " + layout.resultKind);
        }
    }

    private void setupOfNecessary() {
        if (layout.resultKind == RES_EXISTENCE) return;
        if (columnCount == 0 || fieldCount == 0 || indexMapping == null) setup();
    }

    private void setup() {
        try {
            ResultSetMetaData md = rset.getMetaData();
            columnCount = md.getColumnCount();

            switch (layout.rowKind) {
                case ROW_NONE:
                    fieldCount = 0;
                    break;
                case ROW_ONE_VALUE:
                    fieldCount = 1;
                    break;
                case ROW_MAP_ENTRY:
                    fieldCount = 2;
                    break;
                case ROW_OBJECTS:
                case ROW_PRIMITIVES:
                    fieldCount = layout.columnNames != null
                            ? layout.columnNames.length
                            : columnCount;
                    break;
                case ROW_CORTEGE:
                    fieldCount = layout.cortegeClasses != null
                            ? layout.cortegeClasses.length
                            : layout.columnNames != null
                                ? layout.columnNames.length
                                : columnCount;
            }

            if (layout.columnNames != null) {

                int n = layout.columnNames.length;
                indexMapping = new int[n];
                Map<String,Integer> names = getColumnNames(md);
                for (int i = 0; i < n; i++) {
                    String name = layout.columnNames[i];
                    Integer index = names.get(name);
                    indexMapping[i] = index != null ? index.intValue() : Integer.MIN_VALUE;
                }
            }
            else {
                int n = fieldCount;
                indexMapping = new int[n];
                for (int i = 0; i < n; i++) {
                    indexMapping[i] = i+1;
                }
            }

        }
        catch (SQLException e) {
            columnCount = fieldCount = 0;
            indexMapping = null;
            throw new DBFetchingException("Failed to get metadata: "+e.getMessage(), e, seance.getStatementText());
        }
    }

    @NotNull
    private Map<String,Integer> getColumnNames(final @NotNull ResultSetMetaData md) {
        try {
            int n = md.getColumnCount();
            Map<String,Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            for (int k = 1; k <= n; k++) {
                String name = md.getColumnName(k);
                if (name != null && name.length() > 0) map.put(name, k);
            }
            return map;
        }
        catch (SQLException e) {
            throw new DBFetchingException("Failed to get metadata: "+e.getMessage(), e, seance.getStatementText());
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
                case ROW_OBJECTS: return fetchObjects();
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
    private Serializable[] fetchObjects() {
        return null; // TODO
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
