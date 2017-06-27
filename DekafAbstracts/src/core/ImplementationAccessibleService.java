package org.jetbrains.dekaf.core;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * Allows to access implementation specific services.
 *
 * @author Leonid Bushuev
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
   * @param serviceName   name of the service, see {@link org.jetbrains.dekaf.core.ImplementationAccessibleService.Names}.
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
    String JDBC_DATA_SOURCE     = "jdbc-data-source";
    String JDBC_CONNECTION      = "jdbc-connection";
    String JDBC_STATEMENT       = "jdbc-statement";
    String JDBC_RESULT_SET      = "jdbc-result-set";
    String JDBC_METADATA        = "jdbc-metadata";

  }

}
