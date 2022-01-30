package net.tsekot.persistence;

import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManagerImpl extends BaseDataSource implements TransactionManager {

    private final static Logger logger = Logger.getLogger(TransactionManagerImpl.class);

    private final DataSource dataSource;
    private ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    public TransactionManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T, E extends Exception> T execute(UnitOfWork<T, E> work) throws E, SQLException {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connectionThreadLocal.set(connection);
            T call = work.call();
            connection.commit();
            return call;
        } catch (SQLException e) {
            connection.rollback();
            logger.error(e);
            throw e;
        } finally {
            connectionThreadLocal.set(null);
            connection.close();
        }
    }

    @Override
    public <T, E extends Exception> T execute(UnitOfWork<T, E> work, int transactionIsolation) throws E, SQLException {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(transactionIsolation);
            connectionThreadLocal.set(connection);
            T call = work.call();
            connection.commit();
            return call;
        } catch (SQLException e) {
            connection.rollback();
            logger.error(e);
            throw e;
        } finally {
            connectionThreadLocal.set(null);
            connection.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            return connection;
        } else {
            throw new IllegalStateException();
        }
    }
}
