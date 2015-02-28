package org.jetbrains.dba.fakedb.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;



/**
 * @author Leonid Bushuev from JetBrains
 */
class BoringSchema {

  final ConcurrentMap<String,BoringTable> tables =
    new ConcurrentHashMap<String, BoringTable>();

}
