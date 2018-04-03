package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Mysql;
import org.jetbrains.dekaf.Rdbms;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Set;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MysqlTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_MYSQL = Collections.singleton(Mysql.RDBMS);


  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_MYSQL;
  }


  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return (DBTestHelper) Proxy.newProxyInstance(
        MysqlTestHelperFactory.class.getClassLoader(),
        new Class[] {DBTestHelper.class},
        new InvocationHandler() {
          final DBTestHelper mysql = new MysqlTestHelper(facade);
          final DBTestHelper memsql = new MemsqlTestHelper(facade);
          private boolean isMemSql(final DBFacade db) {
            return db.isConnected() && Mysql.MEMSQL_FLAVOUR.equals(db.getConnectionInfo().rdbmsName);
          }
          @Override
          public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            try {
              return method.invoke(isMemSql(facade) ? memsql : mysql, args);
            }
            catch (InvocationTargetException e) {
              throw e.getCause();
            }
          }
        });
  }

}
