package org.jetbrains.jdba.core;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * Allows to access implementation specific services.
 *
 * @author Leonid Bushuev from JetBrains
 **/
public interface ImplementationAccessibleService {

  /**
   * Allows to access an implementation-specific service.
   *
   * <p>
   *
   * </p>
   *
   * @param serviceClass  class or interface that is implemented by this service.
   * @param serviceName   name of the service, see {@link org.jetbrains.jdba.core.ImplementationAccessibleService.Names}.
   * @param <I>           type of class or interface that is implemented by this service.
   * @return              the service.
   * @throws ClassCastException   when the requested service doesn't extend the specified class
   *                              and doesn't implement the specified interface.
   */
  @Nullable
  <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                           @NotNull @MagicConstant(valuesFromClass = Names.class) final String serviceName)
    throws ClassCastException;


  interface Names {

    String INTERMEDIATE_SERVICE = "intermediate-service";
    String CONNECTION_POOL      = "connection-pool";
    String JDBC_DRIVER          = "jdbc-driver";
    String JDBC_CONNECTION      = "jdbc-connection";

  }

}
