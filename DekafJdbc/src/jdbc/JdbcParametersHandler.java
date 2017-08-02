package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBParameterSettingException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import static java.lang.String.format;



/**
 * Service for working with parameters.
 * Currently it's a static class,
 * but later it will be converted to an instantiable service.
 *
 * @author Leonid Bushuev from JetBrains
 */
abstract class JdbcParametersHandler {



    static void assignParameter(@NotNull final PreparedStatement stmt,
                                final int index,
                                @Nullable final Object object)
            throws SQLException
    {
        if (object == null) {
            assignNull(stmt, index);
            return;
        }

        String setter = null;
        try {
            if (object instanceof Boolean) {
                setter = "setBoolean";
                stmt.setBoolean(index, (Boolean) object);
            }
            else if (object instanceof Byte) {
                setter = "setByte";
                stmt.setByte(index, (Byte) object);
            }
            else if (object instanceof Short) {
                setter = "setShort";
                stmt.setShort(index, (Short) object);
            }
            else if (object instanceof Integer) {
                setter = "setInt";
                stmt.setInt(index, (Integer) object);
            }
            else if (object instanceof Float) {
                setter = "setFloat";
                stmt.setFloat(index, (Float) object);
            }
            else if (object instanceof Double) {
                setter = "setDouble";
                stmt.setDouble(index, (Double) object);
            }
            else if (object instanceof BigInteger) {
                setter = "setBigDecimal";
                BigDecimal bigDecimal = new BigDecimal(object.toString());
                stmt.setBigDecimal(index, bigDecimal);
            }
            else if (object instanceof BigDecimal) {
                setter = "setBigDecimal";
                stmt.setBigDecimal(index, (BigDecimal) object);
            }
            else if (object instanceof Long) {
                setter = "setLong";
                stmt.setLong(index, (Long) object);
            }
            else if (object instanceof Character) {
                setter = "setString";
                stmt.setString(index, object.toString());
            }
            else if (object instanceof String) {
                setter = "setString";
                stmt.setString(index, (String) object);
            }
            else if (object instanceof java.sql.Date) {
                setter = "setDate";
                stmt.setDate(index, (java.sql.Date) object);
            }
            else if (object instanceof Timestamp) {
                setter = "setTimestamp";
                stmt.setTimestamp(index, (Timestamp) object);
            }
            else if (object instanceof java.sql.Time) {
                setter = "setTime";
                stmt.setTime(index, (java.sql.Time) object);
            }
            else if (object instanceof java.util.Date) {
                setter = "setTimestamp";
                stmt.setTimestamp(index, new Timestamp(((java.util.Date) object).getTime()));
            }
            else if (object instanceof byte[]) {
                setter = "setBytes";
                stmt.setBytes(index, (byte[]) object);
            }
            else {
                setter = "setObject";
                stmt.setObject(index, object);
            }
        }
        catch (Exception e) {
            //noinspection ConstantConditions
            String message = setter != null
                    ? format("A problem with setting parameter %d using %s(). The original value class is %s. Exception %s: %s",
                             index, setter,
                             object.getClass().getCanonicalName(),
                             e.getClass().getSimpleName(),
                             e.getMessage())
                    : format("An unexpected problem with setting parameter %d. Exception %s: %s",
                             index, e.getClass().getSimpleName(), e.getMessage());
            throw new DBParameterSettingException(message, e, null);
        }
    }

    private static void assignNull(final @NotNull PreparedStatement stmt, final int index) {
        try {
            stmt.setNull(index, Types.BIT);
        }
        catch (Exception e) {
            String message = format("A problem with setting NULL to parameter %d.", index);
            throw new DBParameterSettingException(message, e, null);
        }
    }
}
