package org.jetbrains.dba.access;

/*
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
*/

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
class OracleSpecificStuff {


  private static final String STRING_ARRAY_SQL_TYPE = "SYS.TXNAME_ARRAY";


  static void assignOracleArray(PreparedStatement stmt, int index, String[] javaArray) throws SQLException {
    final Connection connection = stmt.getConnection();

    ClassLoader cl = stmt.getClass().getClassLoader();

    // ArrayDescriptor arrayDescriptor = ArrayDescriptor.createDescriptor(STRING_ARRAY_SQL_TYPE, connection);

    Class arrayDescriptorClass = getClass(cl, "oracle.sql.ArrayDescriptor");
    Method arrayDescriptorCreator = getMethod(arrayDescriptorClass, "createDescriptor", String.class, Connection.class);
    Object arrayDescriptor = invokeStatic(arrayDescriptorCreator, STRING_ARRAY_SQL_TYPE, connection);

    // ARRAY oracleArray = new ARRAY(arrayDescriptor, connection, javaArray);

    Class<java.sql.Array> arrayClass = getClass(cl, "oracle.sql.ARRAY");
    Constructor<java.sql.Array> arrayConstructor =
      getConstructor(arrayClass, new Class[]{arrayDescriptorClass, Connection.class, Object.class});
    java.sql.Array oracleArray = instantiate(arrayConstructor, arrayDescriptor, connection, javaArray);

    // OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement) stmt;
    // oraclePreparedStatement.setARRAY(index, oracleArray);

    Class<PreparedStatement> oraclePreparedStatementClass = getClass(cl, "oracle.jdbc.OraclePreparedStatement");
    Method setArrayMethod = getMethod(oraclePreparedStatementClass, "setARRAY", int.class, arrayClass);
    invoke(stmt, setArrayMethod, index, oracleArray);

    /*
    ArrayDescriptor arrayDescriptor = ArrayDescriptor.createDescriptor(STRING_ARRAY_SQL_TYPE, connection);
    ARRAY oracleArray = new ARRAY(arrayDescriptor, connection, javaArray);
    OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement) stmt;
    oraclePreparedStatement.setARRAY(index, oracleArray);
    */
  }


  private static <T> T instantiate(final Constructor<T> arrayConstructor, final Object... params) {
    try {
      return arrayConstructor.newInstance(params);
    }
    catch (InstantiationException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    catch (InvocationTargetException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }


  private static <T> Constructor<T> getConstructor(Class<T> klass, final Class[] classes) {
    try {
      return klass.getConstructor(classes);
    }
    catch (NoSuchMethodException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }


  @SuppressWarnings("unchecked")
  private static <T> Class<T> getClass(final ClassLoader cl, final String className) {
    try {
      final Class<?> klass = cl.loadClass(className);
      return (Class<T>) klass;
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }


  private static Method getMethod(final Class<?> klass, final String methodName,  Class<?>... parameterTypes) {
    try {
      return klass.getMethod(methodName, parameterTypes);
    }
    catch (NoSuchMethodException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    catch (SecurityException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }


  @SuppressWarnings("unchecked")
  private static <R> R invokeStatic(final Method method, final Object... params) {
    try {
      Object result = method.invoke(null, params);
      return (R) result;
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    catch (InvocationTargetException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private static <R> R invoke(@NotNull final Object classInstance, final Method method, final Object... params) {
    try {
      Object result = method.invoke(classInstance, params);
      return (R) result;
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    catch (InvocationTargetException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

}
