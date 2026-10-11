package com.reshmamart.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnectionPool {
    private static volatile HikariDataSource dataSource;

    private DBConnectionPool() {}

    public static synchronized void init(Properties props) {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url", "jdbc:h2:file:./data/reshmamart;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1"));
        config.setDriverClassName(props.getProperty("db.driver", "org.h2.Driver"));
        config.setUsername(props.getProperty("db.user", "sa"));
        config.setPassword(props.getProperty("db.password", ""));

        int maxPoolSize = Integer.parseInt(props.getProperty("db.pool.maximum", "10"));
        int minIdle = Integer.parseInt(props.getProperty("db.pool.minimumIdle", "2"));
        long idleTimeout = Long.parseLong(props.getProperty("db.pool.idleTimeout", "30000"));
        long maxLifetime = Long.parseLong(props.getProperty("db.pool.maxLifetime", "1800000"));
        long connectionTimeout = Long.parseLong(props.getProperty("db.pool.connectionTimeout", "10000"));

        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("reshmamartHikariPool");

        dataSource = new HikariDataSource(config);
    }

    public static synchronized void init(HikariDataSource customDataSource) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        dataSource = customDataSource;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("HikariDataSource is not initialized or closed.");
        }
        return dataSource.getConnection();
    }

    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
        }
    }

    public static boolean isHealthy() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}
