package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.intermediate.IntegralIntermediateFederatedProvider;
import org.jetbrains.jdba.intermediate.IntegralIntermediateRdbmsProvider;
import org.junit.Test;
import sun.misc.Service;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcJarProvidersTest {

  @Test
  public void check_federated_provider_service_is_published() {
    final Iterator providers = Service.providers(IntegralIntermediateFederatedProvider.class);

    boolean ok = providers.hasNext();
    assertThat(ok).isTrue();

    Object svc = providers.next();

    assertThat(svc).isInstanceOf(IntegralIntermediateFederatedProvider.class);
    assertThat(svc).isInstanceOf(JdbcIntermediateFederatedProvider.class);
  }

  @Test
  public void check_unknown_database_provider_service_is_published() {
    final Iterator providers = Service.providers(IntegralIntermediateRdbmsProvider.class);

    boolean ok = providers.hasNext();
    assertThat(ok).isTrue();

    Object svc = providers.next();

    assertThat(svc).isInstanceOf(IntegralIntermediateRdbmsProvider.class);
    assertThat(svc).isInstanceOf(JdbcIntermediateRdbmsProvider.class);
  }

}
