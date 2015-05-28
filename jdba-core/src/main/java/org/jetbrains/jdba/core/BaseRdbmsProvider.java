package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.intermediate.AdaptIntermediateRdbmsProvider;
import org.jetbrains.jdba.intermediate.IntegralIntermediateFacade;
import org.jetbrains.jdba.intermediate.IntegralIntermediateRdbmsProvider;
import org.jetbrains.jdba.intermediate.PrimeIntermediateRdbmsProvider;

import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseRdbmsProvider implements DBRdbmsProvider {

  //// STATE \\\\

  @NotNull
  private final IntegralIntermediateRdbmsProvider myIntermediateProvider;


  //// CONSTRUCTORS \\\\

  public BaseRdbmsProvider(@NotNull final PrimeIntermediateRdbmsProvider primeIntermediateProvider) {
    this(adaptIntermediateProvider(primeIntermediateProvider));
  }

  @NotNull
  private static IntegralIntermediateRdbmsProvider adaptIntermediateProvider(final @NotNull PrimeIntermediateRdbmsProvider primeIntermediateProvider) {
    return new AdaptIntermediateRdbmsProvider(primeIntermediateProvider);
  }

  public BaseRdbmsProvider(@NotNull final IntegralIntermediateRdbmsProvider integralIntermediateProvider) {
    myIntermediateProvider = integralIntermediateProvider;
  }



  //// IMPLEMENTATION \\\\

  @NotNull
  @Override
  public Rdbms rdbms() {
    return myIntermediateProvider.rdbms();
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return myIntermediateProvider.connectionStringPattern();
  }


  @NotNull
  @Override
  public BaseFacade openFacade(@NotNull final String connectionString,
                               @Nullable final Properties connectionProperties,
                               final int connectionsLimit,
                               final boolean connect) {
    boolean ok = false;
    final IntegralIntermediateFacade intermediateFacade =
            myIntermediateProvider.openFacade(connectionString,
                                              connectionProperties,
                                              connectionsLimit);
    try {
      final BaseFacade facade = new BaseFacade(intermediateFacade);
      if (connect) {
        facade.connect();
      }
      ok = true;
      return facade;
    }
    finally {
      if (!ok) {
        intermediateFacade.disconnect();
      }
    }
  }


  //// AUXILIARY AND DIAGNOSTIC FUNCTIONS \\\\


}
