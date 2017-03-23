package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ResultLayout;
import org.jetbrains.dekaf.core.RowLayout;
import org.jetbrains.dekaf.exceptions.DBFetchingException;
import org.jetbrains.dekaf.util.NameAndClass;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import static java.lang.String.format;
import static org.jetbrains.dekaf.util.Classes.defaultConstructorOf;
import static org.jetbrains.dekaf.util.Collects.arrayToString;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateStructCollectingCursor<T> extends AdaptIntermediateCursor<T,List<Object[]>> {

  /**
   * The result layout.
   * Comes from the {@link org.jetbrains.dekaf.sql.SqlQuery}.
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
  private final Field[] myRowClassFields;

  private transient Collection myContainer;

  private boolean myHasRows;


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
    myRowConstructor = Classes.defaultConstructorOf(rowClass);
    myRowConstructor.setAccessible(true);

    final NameAndClass[] components = myResultLayout.row.components;
    final int n = components.length;
    myRowClassFields = new Field[n];

    myHasRows = remoteCursor.hasRows();
    if (!hasRows()) {
      return;
    }

    boolean somethingMatched = false;

    for (int i = 0; i < n; i++) {
      final String name = components[i].name;

      final Field field;
      try {
        field = rowClass.getDeclaredField(name);
        field.setAccessible(true);
        myRowClassFields[i] = field;
        somethingMatched = true;
      }
      catch (NoSuchFieldException e) {
        // it's strange
      }
    }

    if (!somethingMatched) {
      String msg = String.format("The query result and the class %s have no common fields. Fields of the class: [%s].",
                                 rowClass.getName(), Collects.arrayToString(components, ", "));
      throw new IllegalStateException(msg);
    }
  }


  @Override
  public boolean hasRows() {
    return myHasRows && super.hasRows();
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
      case Kind.SINGLE_ROW:
      case Kind.ARRAY:
      case Kind.LIST:
        myContainer = new ArrayList(capacity);
        break;
      case Kind.SET:
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
      throw new DBFetchingException(String.format("Failed to instantiate class %s: error %s: %s",
                                                  myResultLayout.row.rowClass.getName(),
                                                  e.getClass().getSimpleName(),
                                                  e.getMessage()),
                                    e, null);
    }

    for (int i = 0, n = myRowClassFields.length; i < n; i++) {
      final Field f = myRowClassFields[i];
      final Object value = componentValues[i];

      if (value == null) continue;

      try {
        f.set(row, value);
      }
        catch (IllegalAccessException e) {
          throw new DBFetchingException(format("Failed to assign field %s of class %s with a value of class %s: error %s: %s. The value: \"%s\"",
                                               f.getName(), row.getClass().getName(), value.getClass().getSimpleName(),
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
      case Kind.SINGLE_ROW:
        result = !myContainer.isEmpty() ? (T) myContainer.iterator().next() : null;
        break;
      case Kind.ARRAY:
        int n = myContainer.size();
        Object[] array = (Object[]) Array.newInstance(myResultLayout.row.rowClass, n);
        result = (T) myContainer.toArray(array);
        break;
      case Kind.LIST:
        result = (T) myContainer;
        break;
      case Kind.SET:
        result = (T) myContainer;
        break;
      default:
        result = null; // to be compilable
        assert false;
    }

    return result;
  }


}
