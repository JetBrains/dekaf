package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateRdbmsProvider implements IntegralIntermediateRdbmsProvider {

  @NotNull
  private final Rdbms myRdbms;

  @NotNull
  private final PrimeIntermediateRdbmsProvider myRemoteProvider;

  public AdaptIntermediateRdbmsProvider(@NotNull final PrimeIntermediateRdbmsProvider remoteProvider) {
    myRemoteProvider = remoteProvider;
    myRdbms = remoteProvider.rdbms();
  }


  @NotNull
  @Override
  public Rdbms rdbms() {
    return myRdbms;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {return myRemoteProvider.connectionStringPattern();}

  @Override
  public byte specificity() {return myRemoteProvider.specificity();}

  @NotNull
  @Override
  public IntegralIntermediateFacade openFacade(@NotNull final String connectionString,
                                               @Nullable final Properties connectionProperties,
                                               final int connectionsLimit) {
    final PrimeIntermediateFacade remoteFacade = myRemoteProvider.openFacade(connectionString,
                                                                             connectionProperties,
                                                                             connectionsLimit);
    return new AdaptIntermediateFacade(remoteFacade);
  }

  @NotNull
  @Override
  public Class<? extends DBExceptionRecognizer> getExceptionRecognizerClass() {
    return myRemoteProvider.getExceptionRecognizerClass();
  }

  @NotNull
  @Override
  public DBExceptionRecognizer getExceptionRecognizer() {
    Class<? extends DBExceptionRecognizer> erClass = myRemoteProvider.getExceptionRecognizerClass();

    try {
      // try to use it's instance
      Field instanceField = erClass.getDeclaredField("INSTANCE");
      if (instanceField != null && Modifier.isStatic(instanceField.getModifiers())) {
        return (DBExceptionRecognizer) instanceField.get(null);
      }
      // try to instantiate
      return erClass.newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    throw new IllegalStateException("Unknown how to get an instance of class "+erClass.getName());
  }


  @Nullable
  @Override
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull final String serviceName) throws ClassCastException {
    return myRemoteProvider.getSpecificService(serviceClass, serviceName);
  }
}
