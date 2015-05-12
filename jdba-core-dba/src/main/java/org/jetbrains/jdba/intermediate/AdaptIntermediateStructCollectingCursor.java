package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.ResultLayout;
import org.jetbrains.jdba.exceptions.DBFetchingException;
import org.jetbrains.jdba.util.NameAndClass;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import static java.lang.String.format;
import static org.jetbrains.jdba.util.Classes.defaultConstructorOf;



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
  private Constructor myRowConstructor;

  @NotNull
  private Field[] myRowClassFields;

  private transient Collection myContainer;




  public AdaptIntermediateStructCollectingCursor(@NotNull final PrimeIntermediateCursor<List<Object[]>> remoteCursor,
                                                 @NotNull final ResultLayout<T> resultLayout) {
    super(remoteCursor);
    assert resultLayout.kind == ResultLayout.Kind.SINGLE_ROW
        || resultLayout.kind == ResultLayout.Kind.ARRAY
        || resultLayout.kind == ResultLayout.Kind.LIST
        || resultLayout.kind == ResultLayout.Kind.SET;

    final Class<?> rowClass = resultLayout.row.rowClass;
    myResultLayout = resultLayout;
    myRowConstructor = defaultConstructorOf(rowClass);
    myRowConstructor.setAccessible(true);

    final NameAndClass[] components = myResultLayout.row.components;
    final int n = components.length;
    myRowClassFields = new Field[n];
    for (int i = 0; i < n; i++) {
      Field field;
      try {
        field = rowClass.getDeclaredField(components[i].name);
        field.setAccessible(true);
      }
      catch (NoSuchFieldException e) {
        field = null;
      }
      myRowClassFields[i] = field;
    }
  }

  @Override
  public synchronized T fetch() {
    List<Object[]> data = myRemoteCursor.fetch();
    int n = data.size();

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

    for (int i = 0, n = componentValues.length; i < n; i++) {
      final Object value = componentValues[i];
      if (value != null) {
        Field f = myRowClassFields[i];
        if (f != null) {
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
