package org.jetbrains.dekaf.jdbc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


@Tag("basic")
class JdbcFacadeUnitTest extends JdbcUnitTestCase {

    @Test
    void activate() {
        hmFacade.activate();
        assertThat(hmFacade.isActive()).isTrue();
        hmFacade.deactivate();
        assertThat(hmFacade.isActive()).isFalse();
    }


    @Test
    void obtainConnection() throws SQLException {
        hmFacade.activate();
        Connection connection = hmFacade.obtainConnection();
        assertThat(connection.isClosed()).isFalse();
    }


    @Test
    void openSession_closeSession() {
        hmFacade.activate();

        assertThat(hmFacade.countSessions()).isEqualTo(0);

        JdbcSession session1 = hmFacade.openSession();
        JdbcSession session2 = hmFacade.openSession();

        assertThat(hmFacade.countSessions()).isEqualTo(2);

        session1.close();
        session2.close();

        assertThat(hmFacade.countSessions()).isEqualTo(0);
    }


    @Test
    void openSession_deactivate() {
        hmFacade.activate();

        assertThat(hmFacade.countSessions()).isEqualTo(0);

        hmFacade.openSession();
        hmFacade.openSession();
        hmFacade.openSession();

        assertThat(hmFacade.countSessions()).isEqualTo(3);

        hmFacade.deactivate();

        assertThat(hmFacade.countSessions()).isEqualTo(0);
    }


}
