package net.tsekot.persistence;

import java.sql.SQLException;

public interface UnitOfWork<T, E extends Exception> {

    public T call() throws E, SQLException;
}
