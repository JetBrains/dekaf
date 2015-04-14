package org.jetbrains.jdba.core;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.errors.DBPreparingError;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;



/**
 * Value getters factory.
 * <p/>
 * Stateless service.
 *
 * @author Leonid Bushuev from JetBrains
 */
public final class ValueGetters {

  //// INTERNAL STRUCTURES \\\\

  private static final class SpecificKey {
    final int jdbcType;
    final @NotNull Class<?> desiredClass;

    SpecificKey(final int jdbcType, @NotNull final Class<?> desiredClass) {
      this.jdbcType = jdbcType;
      this.desiredClass = desiredClass;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SpecificKey that = (SpecificKey) o;

      return this.jdbcType == that.jdbcType && this.desiredClass == that.desiredClass;
    }

    @Override
    public int hashCode() {
      return Math.abs(jdbcType)*17 + desiredClass.hashCode();
    }
  }


  //// INSTANCE PLENTY \\\\

  @NotNull
  private static final Map<Class<?>, ValueGetter<?>> NORMAL_GETTERS =
    ImmutableMap.<Class<?>, ValueGetter<?>>builder()
          .put(boolean.class, IntBoolGetter.INSTANCE)
          .put(Boolean.class, IntBoolGetter.INSTANCE)
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
          .put(java.util.Date.class, JavaDateGetter.INSTANCE)
          .put(java.sql.Date.class, DateGetter.INSTANCE)
          .put(java.sql.Timestamp.class, TimestampGetter.INSTANCE)
          .put(java.sql.Time.class, TimeGetter.INSTANCE)
          .put(Object.class, ObjectGetter.INSTANCE)
          .build();

  private static final Map<SpecificKey, ValueGetter<?>> SPECIFIC_GETTERS =
    ImmutableMap.<SpecificKey, ValueGetter<?>>builder()
          .put(new SpecificKey(Types.BOOLEAN, boolean.class), BoolBoolGetter.INSTANCE)
          .put(new SpecificKey(Types.BOOLEAN, Boolean.class), BoolBoolGetter.INSTANCE)
          .put(new SpecificKey(Types.BOOLEAN, byte.class), BoolByteGetter.INSTANCE)
          .put(new SpecificKey(Types.BOOLEAN, Byte.class), BoolByteGetter.INSTANCE)
          .put(new SpecificKey(Types.BOOLEAN, short.class), BoolShortGetter.INSTANCE)
          .put(new SpecificKey(Types.BOOLEAN, Short.class), BoolShortGetter.INSTANCE)
          .put(new SpecificKey(Types.BOOLEAN, int.class), BoolIntGetter.INSTANCE)
          .put(new SpecificKey(Types.BOOLEAN, Integer.class), BoolIntGetter.INSTANCE)
          .put(new SpecificKey(Types.BIT, boolean.class), BoolBoolGetter.INSTANCE)
          .put(new SpecificKey(Types.BIT, Boolean.class), BoolBoolGetter.INSTANCE)
          .put(new SpecificKey(Types.BIT, byte.class), BoolByteGetter.INSTANCE)
          .put(new SpecificKey(Types.BIT, Byte.class), BoolByteGetter.INSTANCE)
          .put(new SpecificKey(Types.BIT, short.class), BoolShortGetter.INSTANCE)
          .put(new SpecificKey(Types.BIT, Short.class), BoolShortGetter.INSTANCE)
          .put(new SpecificKey(Types.BIT, int.class), BoolIntGetter.INSTANCE)
          .put(new SpecificKey(Types.BIT, Integer.class), BoolIntGetter.INSTANCE)
          .put(new SpecificKey(Types.TINYINT, boolean.class), IntBoolGetter.INSTANCE)
          .put(new SpecificKey(Types.TINYINT, Boolean.class), IntBoolGetter.INSTANCE)
          .put(new SpecificKey(Types.SMALLINT, boolean.class), IntBoolGetter.INSTANCE)
          .put(new SpecificKey(Types.SMALLINT, Boolean.class), IntBoolGetter.INSTANCE)
          .put(new SpecificKey(Types.INTEGER, boolean.class), IntBoolGetter.INSTANCE)
          .put(new SpecificKey(Types.INTEGER, Boolean.class), IntBoolGetter.INSTANCE)
          .build();


  @NotNull
  @SuppressWarnings("unchecked")
  static <W> ValueGetter<W> of(final int jdbcType, @NotNull final Class<W> clazz) {
    ValueGetter<W> getter = find(jdbcType, clazz);
    if (getter == null) throw new DBPreparingError("Unknown how to get a value of class "+clazz.getSimpleName());
    return getter;
  }


  @Nullable
  @SuppressWarnings("unchecked")
  static <W> ValueGetter<W> find(final int jdbcType, @NotNull final Class<W> clazz) {
    ValueGetter<?> getter = null;
    if (jdbcType != Types.OTHER) getter = SPECIFIC_GETTERS.get(new SpecificKey(jdbcType, clazz));
    if (getter == null) getter = NORMAL_GETTERS.get(clazz);
    return (ValueGetter<W>) getter;
  }



  //// GETTERS \\\\


  static final class BoolBoolGetter extends ValueGetter<Boolean> {
    @Override
    @Nullable
    Boolean getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final boolean value = rset.getBoolean(index);
      return rset.wasNull() ? null : value;
    }


    static final BoolBoolGetter INSTANCE = new BoolBoolGetter();
  }

  @SuppressWarnings("UnnecessaryBoxing")
  static final class BoolByteGetter extends ValueGetter<Byte> {
    @Override
    @Nullable
    Byte getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final boolean value = rset.getBoolean(index);
      return rset.wasNull() ? null : value ? _1_ : _0_;
    }

    private final static Byte _1_ = Byte.valueOf((byte)1);
    private final static Byte _0_ = Byte.valueOf((byte)0);

    final static BoolByteGetter INSTANCE = new BoolByteGetter();
  }

  @SuppressWarnings("UnnecessaryBoxing")
  static final class BoolShortGetter extends ValueGetter<Short> {
    @Override
    @Nullable
    Short getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final boolean value = rset.getBoolean(index);
      return rset.wasNull() ? null : value ? _1_ : _0_;
    }

    private final static Short _1_ = Short.valueOf((short)1);
    private final static Short _0_ = Short.valueOf((short)0);

    final static BoolShortGetter INSTANCE = new BoolShortGetter();
  }

  @SuppressWarnings("UnnecessaryBoxing")
  static final class BoolIntGetter extends ValueGetter<Integer> {
    @Override
    @Nullable
    Integer getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final boolean value = rset.getBoolean(index);
      return rset.wasNull() ? null : value ? _1_ : _0_;
    }

    private final static Integer _1_ = Integer.valueOf((int)1);
    private final static Integer _0_ = Integer.valueOf((int)0);

    final static BoolIntGetter INSTANCE = new BoolIntGetter();
  }



  static final class IntBoolGetter extends ValueGetter<Boolean> {
    @Override
    @Nullable
    Boolean getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final int value = rset.getInt(index);
      return rset.wasNull() ? null : value > 0;
    }


    static final IntBoolGetter INSTANCE = new IntBoolGetter();
  }



  static final class ByteGetter extends ValueGetter<Byte> {
    @Override
    @Nullable
    Byte getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final byte value = rset.getByte(index);
      return rset.wasNull() ? null : value;
    }


    final static ByteGetter INSTANCE = new ByteGetter();
  }


  static final class ShortGetter extends ValueGetter<Short> {
    @Override
    @Nullable
    Short getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final short value = rset.getShort(index);
      return rset.wasNull() ? null : value;
    }


    final static ShortGetter INSTANCE = new ShortGetter();
  }



  static final class IntGetter extends ValueGetter<Integer> {
    @Override
    @Nullable
    Integer getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final int value = rset.getInt(index);
      return rset.wasNull() ? null : value;
    }


    final static IntGetter INSTANCE = new IntGetter();
  }



  static final class LongGetter extends ValueGetter<Long> {
    @Override
    @Nullable
    Long getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final long value = rset.getLong(index);
      return rset.wasNull() ? null : value;
    }


    final static LongGetter INSTANCE = new LongGetter();
  }



  static final class FloatGetter extends ValueGetter<Float> {
    @Override
    @Nullable
    Float getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final float value = rset.getFloat(index);
      return rset.wasNull() ? null : value;
    }


    final static FloatGetter INSTANCE = new FloatGetter();
  }



  static final class DoubleGetter extends ValueGetter<Double> {
    @Override
    @Nullable
    Double getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final double value = rset.getDouble(index);
      return rset.wasNull() ? null : value;
    }


    final static DoubleGetter INSTANCE = new DoubleGetter();
  }



  static final class StringGetter extends ValueGetter<String> {
    @Override
    @Nullable
    String getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      return rset.getString(index);
    }


    static final StringGetter INSTANCE = new StringGetter();
  }



  static final class CharGetter extends ValueGetter<Character> {
    @Override
    @Nullable
    Character getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      final String str = rset.getString(index);
      if (rset.wasNull() || str == null || str.length() == 0) {
        return null;
      }
      return str.charAt(0);
    }


    final static CharGetter INSTANCE = new CharGetter();
  }



  static final class JavaDateGetter extends ValueGetter<java.util.Date> {
    @Override
    @Nullable
    java.util.Date getValue(@NotNull final ResultSet rset, final int index)  throws SQLException {
      final Timestamp timestamp = rset.getTimestamp(index);
      return rset.wasNull() ? null : new java.util.Date(timestamp.getTime());
    }


    final static JavaDateGetter INSTANCE = new JavaDateGetter();
  }


  static final class DateGetter extends ValueGetter<java.sql.Date> {
    @Override
    @Nullable
    java.sql.Date getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      return rset.getDate(index);
    }


    final static DateGetter INSTANCE = new DateGetter();
  }


  static final class TimestampGetter extends ValueGetter<java.sql.Timestamp> {
    @Override
    @Nullable
    java.sql.Timestamp getValue(@NotNull final ResultSet rset, final int index)  throws SQLException {
      return rset.getTimestamp(index);
    }


    final static TimestampGetter INSTANCE = new TimestampGetter();
  }


  static final class TimeGetter extends ValueGetter<java.sql.Time> {
    @Override
    @Nullable
    java.sql.Time getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      return rset.getTime(index);
    }


    final static TimeGetter INSTANCE = new TimeGetter();
  }



  static final class ObjectGetter extends ValueGetter<Object> {
    @Override
    @Nullable
    Object getValue(@NotNull final ResultSet rset, final int index) throws SQLException {
      return rset.getObject(index);
    }


    static final ObjectGetter INSTANCE = new ObjectGetter();
  }


  static final class DumbNullGetter extends ValueGetter<Object> {
    @Nullable
    @Override
    Object getValue(@NotNull final ResultSet rset, final int index) {
      return null;
    }


    static final DumbNullGetter INSTANCE = new DumbNullGetter();
  }

}
