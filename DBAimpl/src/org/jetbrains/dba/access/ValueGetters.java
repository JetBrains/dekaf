package org.jetbrains.dba.access;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;



/**
 * Value getters factory.
 * <p/>
 * Stateless service.
 *
 * @author Leonid Bushuev from JetBrains
 */
public final class ValueGetters {

  //// INSTANCE PLENTY \\\\

  @NotNull
  private static final Map<Class, ValueGetter<?>> getters
    = ImmutableMap.<Class, ValueGetter<?>>builder()
    .put(boolean.class, BoolGetter.INSTANCE)
    .put(Boolean.class, BoolGetter.INSTANCE)
    .put(byte.class, ByteGetter.INSTANCE)
    .put(Byte.class, ByteGetter.INSTANCE)
    .put(short.class, ShortGetter.INSTANCE)
    .put(Short.class, ShortGetter.INSTANCE)
    .put(int.class, IntGetter.INSTANCE)
    .put(Integer.class, IntGetter.INSTANCE)
    .put(long.class, LongGetter.INSTANCE)
    .put(Long.class, LongGetter.INSTANCE)
    .put(float.class, FloatGetter.INSTANCE)
    .put(Float.class, FloatGetter.INSTANCE)
    .put(double.class, DoubleGetter.INSTANCE)
    .put(Double.class, DoubleGetter.INSTANCE)
    .put(String.class, StringGetter.INSTANCE)
    .put(char.class, CharGetter.INSTANCE)
    .put(Character.class, CharGetter.INSTANCE)
    .put(Object.class, ObjectGetter.INSTANCE)
    .build();


  @Nullable
  @SuppressWarnings("unchecked")
  static <W> ValueGetter<W> of(@NotNull final Class<W> clazz) {
    return (ValueGetter<W>)getters.get(clazz);
  }


  //// SUBCLASSES \\\\



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class BoolGetter extends ValueGetter<Boolean> {
    @Override
    @Nullable
    Boolean getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      final int value = rset.getInt(index);
      return rset.wasNull() ? null : value > 0;
    }


    static final BoolGetter INSTANCE = new BoolGetter();
  }



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class ByteGetter extends ValueGetter<Byte> {
    @Override
    @Nullable
    Byte getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      final byte value = rset.getByte(index);
      return rset.wasNull() ? null : value;
    }


    final static ByteGetter INSTANCE = new ByteGetter();
  }



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class ShortGetter extends ValueGetter<Short> {
    @Override
    @Nullable
    Short getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      final short value = rset.getShort(index);
      return rset.wasNull() ? null : value;
    }


    final static ShortGetter INSTANCE = new ShortGetter();
  }



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class IntGetter extends ValueGetter<Integer> {
    @Override
    @Nullable
    Integer getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      final int value = rset.getInt(index);
      return rset.wasNull() ? null : value;
    }


    final static IntGetter INSTANCE = new IntGetter();
  }



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class LongGetter extends ValueGetter<Long> {
    @Override
    @Nullable
    Long getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      final long value = rset.getLong(index);
      return rset.wasNull() ? null : value;
    }


    final static LongGetter INSTANCE = new LongGetter();
  }



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class FloatGetter extends ValueGetter<Float> {
    @Override
    @Nullable
    Float getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      final float value = rset.getFloat(index);
      return rset.wasNull() ? null : value;
    }


    final static FloatGetter INSTANCE = new FloatGetter();
  }



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class DoubleGetter extends ValueGetter<Double> {
    @Override
    @Nullable
    Double getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      final double value = rset.getDouble(index);
      return rset.wasNull() ? null : value;
    }


    final static DoubleGetter INSTANCE = new DoubleGetter();
  }



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class StringGetter extends ValueGetter<String> {
    @Override
    @Nullable
    String getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      return rset.getString(index);
    }


    static final StringGetter INSTANCE = new StringGetter();
  }



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class CharGetter extends ValueGetter<Character> {
    @Override
    @Nullable
    Character getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      final String str = rset.getString(index);
      if (rset.wasNull() || str == null || str.length() == 0) {
        return null;
      }
      return str.charAt(0);
    }


    final static CharGetter INSTANCE = new CharGetter();
  }



  /**
   * @author Leonid Bushuev from JetBrains
   */
  static final class ObjectGetter extends ValueGetter<Object> {
    @Override
    @Nullable
    Object getValue(@NotNull final ResultSet rset, final int index)
      throws SQLException {
      return rset.getObject(index);
    }


    static final ObjectGetter INSTANCE = new ObjectGetter();
  }
}
