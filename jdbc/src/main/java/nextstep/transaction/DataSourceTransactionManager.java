package nextstep.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import nextstep.jdbc.DataSourceUtils;

public class DataSourceTransactionManager implements PlatformTransactionManager {

    private DataSource dataSource;

    public DataSourceTransactionManager(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TransactionStatus getTransaction(final TransactionDefinition definition) throws TransactionException {
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(definition.getIsolationLevel().getLevel());
            return new TransactionStatus(definition.getIsolationLevel(), definition.getPropagationBehavior(),
                    definition.isReadOnly(), connection);
        } catch (SQLException e) {
            throw new TransactionException();
        }
    }

    @Override
    public void commit(final TransactionStatus status) throws TransactionException {
        Connection connection = status.getConnection();
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new TransactionException(e);
        } finally {
            release(connection);
        }
    }

    @Override
    public void rollback(final TransactionStatus status) throws TransactionException {
        Connection connection = status.getConnection();
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new TransactionException(e);
        } finally {
            release(connection);
        }
    }

    private void release(final Connection connection) {
        try {
            DataSourceUtils.release(connection, dataSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
