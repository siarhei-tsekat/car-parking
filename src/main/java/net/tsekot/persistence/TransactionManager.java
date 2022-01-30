package net.tsekot.persistence;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface TransactionManager extends DataSource {

    <T, E extends Exception> T execute(UnitOfWork<T, E> work) throws E, SQLException;

    <T, E extends Exception> T execute(UnitOfWork<T, E> work, int transactionIsolation) throws E, SQLException;
}
