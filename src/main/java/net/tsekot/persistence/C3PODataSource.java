package net.tsekot.persistence;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class C3PODataSource implements DataSource {

    private static C3PODataSource c3PODataSource;
    private final ComboPooledDataSource comboPooledDataSource;

    public static C3PODataSource getC3PODataSource() {
        if (c3PODataSource == null) {
            c3PODataSource = new C3PODataSource("tsekot", "pwd", "jdbc:sqlserver://localhost:1433;database=parking", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        return c3PODataSource;
    }

    private C3PODataSource(String userName, String password, String jdbcUrl, String driverClass) {
        comboPooledDataSource = new ComboPooledDataSource();
        try {
            comboPooledDataSource.setDriverClass(driverClass);
            comboPooledDataSource.setUser(userName);
            comboPooledDataSource.setPassword(password);
            comboPooledDataSource.setJdbcUrl(jdbcUrl);
            comboPooledDataSource.setInitialPoolSize(3);
            comboPooledDataSource.setMaxPoolSize(10);

        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return comboPooledDataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return comboPooledDataSource.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return comboPooledDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        comboPooledDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        comboPooledDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return comboPooledDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return comboPooledDataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return comboPooledDataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return comboPooledDataSource.isWrapperFor(iface);
    }
}
