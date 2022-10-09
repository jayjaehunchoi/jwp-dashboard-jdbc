package nextstep.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TransactionSynchronizationManagerTest {

    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    void setUp(){
        dataSource = Mockito.mock(DataSource.class);
        connection = Mockito.mock(Connection.class);
    }

    @Test
    void getResourceWhenNull() {
        assertThat(TransactionSynchronizationManager.getResource(dataSource)).isNull();
    }

    @Test
    void bindConnection() {
        TransactionSynchronizationManager.bindConnection(dataSource, connection);
        assertThat(TransactionSynchronizationManager.getResource(dataSource)).isEqualTo(connection);
    }

    @Test
    void release() {
        TransactionSynchronizationManager.bindConnection(dataSource, connection);
        Object releasedConnection = TransactionSynchronizationManager.release(dataSource);

        assertAll(
                () -> assertThat(TransactionSynchronizationManager.getResource(dataSource)).isNull(),
                () -> assertThat(releasedConnection).isEqualTo(connection)
        );
    }
}
