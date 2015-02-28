package org.jetbrains.dba.fakedb.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;



/**
 * @author Leonid Bushuev from JetBrains
 */
class BoringDatabase {

  final ConcurrentMap<String,BoringSchema> schemas =
    new ConcurrentHashMap<String, BoringSchema>();

}
