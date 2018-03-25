package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.InterLayout;
import org.jetbrains.dekaf.inter.InterTask;
import org.jetbrains.dekaf.util.JavaPrimitiveKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.core.TaskKind.TASK_QUERY;
import static org.jetbrains.dekaf.inter.InterResultKind.*;
import static org.jetbrains.dekaf.inter.InterRowKind.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;



@Tag("UnitTest")
public final class JdbcSeanceTest extends JdbcUnitTestCase
{

    @Test
    public void existence_0() {
        InterTask task = new InterTask(TASK_QUERY, "select 12345 where 1 is null");
        InterLayout layout = new InterLayout(RES_EXISTENCE, ROW_NONE, null, null);
        Serializable result = queryPortion(task, layout);
        assertThat(result).isInstanceOf(Boolean.class);
        Boolean b = (Boolean) result;
        assertThat(b).isFalse();
    }

    @Test
    public void existence_1() {
        InterTask task = new InterTask(TASK_QUERY, "select 12345");
        InterLayout layout = new InterLayout(RES_EXISTENCE, ROW_NONE, null, null);
        Serializable result = queryPortion(task, layout);
        assertThat(result).isInstanceOf(Boolean.class);
        Boolean b = (Boolean) result;
        assertThat(b).isTrue();
    }

    @Test
    public void select_123_asSingleValue() {
        InterTask task = new InterTask(TASK_QUERY, "select 123");
        InterLayout layout = new InterLayout(RES_ONE_ROW, ROW_ONE_VALUE, null, Number.class);
        Serializable result = queryPortion(task, layout);
        assertThat(result).isInstanceOf(Number.class);
        int v = ((Number)result).intValue();
        assertThat(v).isEqualTo(123);
    }

    @Test
    public void select_123_asRowOfNumber() {
        InterTask task = new InterTask(TASK_QUERY, "select 123");
        InterLayout layout = new InterLayout(RES_ONE_ROW, ROW_OBJECTS, null, Number.class);
        Serializable result = queryPortion(task, layout);
        assertThat(result).isInstanceOf(Number[].class);
        Number[] row = (Number[]) result;
        assertThat(row).hasSize(1);
        Number number = row[0];
        assertThat(number.intValue()).isEqualTo(123);
    }

    @Test
    public void select_123_asRowOfShort() {
        InterTask task = new InterTask(TASK_QUERY, "select 123");
        InterLayout layout = new InterLayout(RES_ONE_ROW, ROW_PRIMITIVES, JavaPrimitiveKind.JAVA_SHORT, null);
        Serializable result = queryPortion(task, layout);
        assertThat(result).isInstanceOf(short[].class);
        short[] row = (short[]) result;
        assertThat(row).hasSize(1);
        short value = row[0];
        assertThat(value).isEqualTo((short)123);
    }

    @Test
    public void matrix_numbers() {
        InterTask task = new InterTask(TASK_QUERY, "select * from table(x int=(1,4,7), y int=(2,5,8), z int=(3,6,9))");
        InterLayout layout = new InterLayout(RES_TABLE, ROW_OBJECTS, null, Number.class);
        Serializable result = queryPortion(task, layout);
        assertThat(result).isInstanceOf(Number[][].class);
        Number[][] matrix = (Number[][]) result;

        assertThat(matrix[0][0]).isEqualTo(1);
        assertThat(matrix[0][1]).isEqualTo(2);
        assertThat(matrix[0][2]).isEqualTo(3);
        assertThat(matrix[1][0]).isEqualTo(4);
        assertThat(matrix[1][1]).isEqualTo(5);
        assertThat(matrix[1][2]).isEqualTo(6);
        assertThat(matrix[2][0]).isEqualTo(7);
        assertThat(matrix[2][1]).isEqualTo(8);
        assertThat(matrix[2][2]).isEqualTo(9);
    }

    @Test
    public void matrix_cortege() {
        InterTask task = new InterTask(TASK_QUERY, "select * from table(x int=(1,4,7), y int=(2,5,8), z int=(3,6,9))");
        Class<?>[] cortegeClasses = new Class<?>[] { Byte.class, Short.class, Long.class };
        InterLayout layout = new InterLayout(RES_TABLE, ROW_OBJECTS, null, Number.class, cortegeClasses);
        Serializable result = queryPortion(task, layout);
        assertThat(result).isInstanceOf(Number[][].class);
        Number[][] matrix = (Number[][]) result;

        assertThat(matrix[0][0]).isInstanceOf(Byte.class);
        assertThat(matrix[0][1]).isInstanceOf(Short.class);
        assertThat(matrix[0][2]).isInstanceOf(Long.class);
        assertThat(matrix[1][0]).isInstanceOf(Byte.class);
        assertThat(matrix[1][1]).isInstanceOf(Short.class);
        assertThat(matrix[1][2]).isInstanceOf(Long.class);
        assertThat(matrix[2][0]).isInstanceOf(Byte.class);
        assertThat(matrix[2][1]).isInstanceOf(Short.class);
        assertThat(matrix[2][2]).isInstanceOf(Long.class);

        assertThat(matrix[0][0].intValue()).isEqualTo(1);
        assertThat(matrix[0][1].intValue()).isEqualTo(2);
        assertThat(matrix[0][2].intValue()).isEqualTo(3);
        assertThat(matrix[1][0].intValue()).isEqualTo(4);
        assertThat(matrix[1][1].intValue()).isEqualTo(5);
        assertThat(matrix[1][2].intValue()).isEqualTo(6);
        assertThat(matrix[2][0].intValue()).isEqualTo(7);
        assertThat(matrix[2][1].intValue()).isEqualTo(8);
        assertThat(matrix[2][2].intValue()).isEqualTo(9);
    }

    @Test
    public void verticalArrayOfBytes_noData() {
        InterTask task = new InterTask(TASK_QUERY, "select -1 where 1 is null");
        InterLayout layout = new InterLayout(RES_PRIMITIVE_ARRAY, ROW_ONE_VALUE, JavaPrimitiveKind.JAVA_BYTE, null);
        Serializable result = queryPortion(task, layout);
        assertNull(result);
    }

    @Test
    public void verticalArrayOfBytes_smallAmountOfData() {
        InterTask task = new InterTask(TASK_QUERY, "select * from table(id int=(11,22,33,44))");
        InterLayout layout = new InterLayout(RES_PRIMITIVE_ARRAY, ROW_ONE_VALUE, JavaPrimitiveKind.JAVA_BYTE, null);
        Serializable result = queryPortion(task, layout);
        assertThat(result).isInstanceOf(byte[].class);
        byte[] column = (byte[]) result;
        assertThat(column).hasSize(4);
        assertThat(column[0]).isEqualTo((byte)11);
        assertThat(column[1]).isEqualTo((byte)22);
        assertThat(column[2]).isEqualTo((byte)33);
        assertThat(column[3]).isEqualTo((byte)44);
    }

    @Test
    public void verticalArrayOfBytes_largeAmountOfData() {
        InterTask task = new InterTask(TASK_QUERY, "select * from table(id int=(00,11,22,33,44,55,66,77,88,99))");
        InterLayout layout = new InterLayout(RES_PRIMITIVE_ARRAY, ROW_ONE_VALUE, JavaPrimitiveKind.JAVA_BYTE, null);
        Serializable[] result = queryPortions(task, layout, 4, 3);

        byte[] pack1 = (byte[]) result[0];
        assertThat(pack1).hasSize(4);
        assertThat(pack1[0]).isEqualTo((byte)00);
        assertThat(pack1[1]).isEqualTo((byte)11);
        assertThat(pack1[2]).isEqualTo((byte)22);
        assertThat(pack1[3]).isEqualTo((byte)33);

        byte[] pack2 = (byte[]) result[1];
        assertThat(pack2).hasSize(4);
        assertThat(pack2[0]).isEqualTo((byte)44);
        assertThat(pack2[1]).isEqualTo((byte)55);
        assertThat(pack2[2]).isEqualTo((byte)66);
        assertThat(pack2[3]).isEqualTo((byte)77);

        byte[] pack3 = (byte[]) result[2];
        assertThat(pack3).hasSize(2);
        assertThat(pack3[0]).isEqualTo((byte)88);
        assertThat(pack3[1]).isEqualTo((byte)99);
    }


    @Nullable
    private static Serializable queryPortion(final @NotNull InterTask task,
                                             final @NotNull InterLayout layout) {
        Object result =
                hmInSession(session -> {
                    final JdbcSeance seance = session.openSeance();
                    seance.prepare(task);
                    seance.execute();
                    JdbcCursor cursor = seance.openCursor((byte) 0, layout);
                    assertNotNull(cursor);
                    Object portion = cursor.retrievePortion();
                    cursor.close();
                    seance.close();
                    return portion;
                });
        if (result != null) {
            assertThat(result).isInstanceOf(Serializable.class);
        }
        return (Serializable) result;
    }


    @NotNull
    private static Serializable[] queryPortions(final @NotNull InterTask task,
                                                final @NotNull InterLayout layout,
                                                final int portionSize,
                                                final int portionsCount) {
        final Serializable[] result = new Serializable[portionsCount];
        hmInSessionDo(session -> {
            final JdbcSeance seance = session.openSeance();
            seance.prepare(task);
            seance.execute();
            JdbcCursor cursor = seance.openCursor((byte) 0, layout);
            assertNotNull(cursor);
            cursor.setPortionSize(portionSize);
            for (int i = 0; i < portionsCount; i++) {
                Object portion = cursor.retrievePortion();
                if (portion != null) {
                    assertThat(portion).isInstanceOf(Serializable.class);
                }
                result[i] = (Serializable) portion;
            }
            cursor.close();
            seance.close();
        });
        return result;
    }


}
