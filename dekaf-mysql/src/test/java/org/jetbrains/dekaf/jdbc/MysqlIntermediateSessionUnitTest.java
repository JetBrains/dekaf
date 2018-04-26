package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.jdbc.pooling.FakeDataSource;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.MysqlConsts.*;



public class MysqlIntermediateSessionUnitTest {

  private static final FakeDataSource FAKE_DATA_SOURCE = new FakeDataSource();

  @Before
  public void setUp() throws Exception {
    FAKE_DATA_SOURCE.reset();
  }

  @Test
  public void getFetchStrategy_auto_byDefault() {
    MysqlIntermediateFacade facade = createFacade();
    MysqlIntermediateSession session = (MysqlIntermediateSession) facade.openSession();
    assertThat(session.getFetchStrategy()).isEqualTo(FETCH_STRATEGY_AUTO);
  }

  @Test
  public void getFetchStrategy_auto_withoutFacade() {
    MysqlIntermediateSession session =
        new MysqlIntermediateSession(null,
                                     new MysqlExceptionRecognizer(),
                                     FAKE_DATA_SOURCE.getConnection(),
                                     true);
    assertThat(session.getFetchStrategy()).isEqualTo(FETCH_STRATEGY_AUTO);
  }


  @Test
  public void getFetchStrategy_row() {
    MysqlIntermediateFacade facade = createFacade();
    facade.setFetchStrategy(FETCH_STRATEGY_ROW);
    MysqlIntermediateSession session = (MysqlIntermediateSession) facade.openSession();
    assertThat(session.getFetchStrategy()).isEqualTo(FETCH_STRATEGY_ROW);
  }

  @Test
  public void getFetchStrategy_whole() {
    MysqlIntermediateFacade facade = createFacade();
    facade.setFetchStrategy(FETCH_STRATEGY_WHOLE);
    MysqlIntermediateSession session = (MysqlIntermediateSession) facade.openSession();
    assertThat(session.getFetchStrategy()).isEqualTo(FETCH_STRATEGY_WHOLE);
  }


  @NotNull
  private static MysqlIntermediateFacade createFacade() {
    MysqlIntermediateFacade facade =
        new MysqlIntermediateFacade(FAKE_DATA_SOURCE, 7, true, new MysqlExceptionRecognizer());
    facade.connect();
    return facade;
  }


}