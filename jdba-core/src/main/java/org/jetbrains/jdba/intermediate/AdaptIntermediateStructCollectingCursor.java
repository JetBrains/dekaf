package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.ResultLayout;
import org.jetbrains.jdba.core.RowLayout;
import org.jetbrains.jdba.exceptions.DBFetchingException;
import org.jetbrains.jdba.util.NameAndClass;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import static java.lang.String.format;
import static org.jetbrains.jdba.util.Classes.defaultConstructorOf;
import static org.jetbrains.jdba.util.Collects.arrayToString;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateStructCollectingCursor<T> extends AdaptIntermediateCursor<T,List<Object[]>> {

  /**
   * The result layout.
   * Comes from the {@link org.jetbrains.jdba.sql.SqlQuery}.
   */
  @NotNull
  private ResultLayout<T> myResultLayout;

  /**
   * The default (without parameters) constructor
   * that is used to instantiate a row class.
   */
  @NotNull
  private final Constructor myRowConstructor;

  @NotNull
  private FieldAndIndex[] myRowClassFields;

  private transient Collection myContainer;

  private boolean myHasRows;


  private static final class FieldAndIndex {
    final Field field;
    final int index;

    private FieldAndIndex(final Field field, final int index) {
      this.field = field;
      this.index = index;
    }
  }



  public AdaptIntermediateStructCollectingCursor(@NotNull final PrimeIntermediateCursor<List<Object[]>> remoteCursor,
                                                 @NotNull final ResultLayout<T> resultLayout) {
    super(remoteCursor);
    assert resultLayout.kind == ResultLayout.Kind.SINGLE_ROW
        || resultLayout.kind == ResultLayout.Kind.ARRAY
        || resultLayout.kind == ResultLayout.Kind.LIST
        || resultLayout.kind == ResultLayout.Kind.SET;

    assert resultLayout.row.kind == RowLayout.Kind.STRUCT;

    final Class<?> rowClass = resultLayout.row.rowClass;
    myResultLayout = resultLayout;
    myRowConstructor = defaultConstructorOf(rowClass);
    myRowConstructor.setAccessible(true);

    myHasRows = remoteCursor.hasRows();
    if (!hasRows()) {
      myRowClassFields = new FieldAndIndex[0];
      return;
    }

    final String[] columnNames = remoteCursor.getColumnNames();

    final NameAndClass[] components = myResultLayout.row.components;
    final int n = components.length;

    ArrayList<FieldAndIndex> fields = new ArrayList<FieldAndIndex>(n);
    for (int i = 0; i < n; i++) {
      final String name = components[i].name;
      final int columnIndex = indexOfStringInArray(name, columnNames);
      if (columnIndex < 0) continue;

      final Field field;
      try {
        field = rowClass.getDeclaredField(name);
        field.setAccessible(true);
      }
      catch (NoSuchFieldException e) {
        continue;
      }

      fields.add(new FieldAndIndex(field, columnIndex));
    }

    if (fields.isEmpty()) {
      String msg = format("The query result and the class %s have no common fields. Fields of the query result: [%s], fields of the class: [%s].",
                          rowClass.getName(), arrayToString(columnNames, ", "), arrayToString(components, ", "));
      throw new IllegalStateException(msg);
    }

    myRowClassFields = fields.toArray(new FieldAndIndex[fields.size()]);
  }


  private static int indexOfStringInArray(@NotNull final String stringToSearch, @NotNull final String[] strings) {
    for (int i = 0, n = strings.length; i < n; i++) {
      String string = strings[i];
      if (stringToSearch.equalsIgnoreCase(string)) return i;
    }
    return Integer.MIN_VALUE;
  }


  @Override
  public synchronized T fetch() {
    List<Object[]> data;
    int n;
    if (hasRows()) {
      data = myRemoteCursor.fetch();
      n = data.size();
    }
    else {
      data = Collections.emptyList();
      n = 0;
    }

    prepareContainer(n);
    for (int i = 0; i < n; i++) {
      Object row = makeRow(data.get(i));
      //noinspection unchecked
      myContainer.add(row);
    }

    return completeCollection();
  }

  private void prepareContainer(final int capacity) {
    switch (myResultLayout.kind) {
      case SINGLE_ROW:
      case ARRAY:
      case LIST:
        myContainer = new ArrayList(capacity);
        break;
      case SET:
        myContainer = myResultLayout.sorted
                    ? new TreeSet()
                    : new HashSet(capacity);
        break;
      default:
        throw new IllegalStateException("Unknown how to collect a " + myResultLayout.kind);
    }
  }

  private Object makeRow(final Object[] componentValues) {
    final Object row;
    try {
      row = myRowConstructor.newInstance();
    }
    catch (Exception e) {
      throw new DBFetchingException(format("Failed to instantiate class %s: error %s: %s",
                                           myResultLayout.row.rowClass.getName(),
                                           e.getClass().getSimpleName(),
                                           e.getMessage()),
                                    e, null);
    }

    for (FieldAndIndex f : myRowClassFields) {
      final Object value = f.index < componentValues.length
                         ? componentValues[f.index]
                         : null;

      if (value == null) continue;
      try {
        f.field.set(row, value);
      }
        catch (IllegalAccessException e) {
          throw new DBFetchingException(format("Failed to assign field %s of class %s with a value of class %s: error %s: %s. The value: \"%s\"",
                                               f.field.getName(), row.getClass().getName(), value.getClass().getSimpleName(),
                                               e.getClass().getSimpleName(), e.getMessage(),
                                               value.toString()),
                                        e, null);
        }
    }

    return row;
  }


  @SuppressWarnings("unchecked")
  private T completeCollection() {
    final T result;

    switch (myResultLayout.kind) {
      case SINGLE_ROW:
        result = !myContainer.isEmpty() ? (T) myContainer.iterator().next() : null;
        break;
      case ARRAY:
        int n = myContainer.size();
        Object[] array = (Object[]) Array.newInstance(myResultLayout.row.rowClass, n);
        result = (T) myContainer.toArray(array);
        break;
      case LIST:
        result = (T) myContainer;
        break;
      case SET:
        result = (T) myContainer;
        break;
      default:
        result = null; // to be compilable
        assert false;
    }

    return result;
  }




}
