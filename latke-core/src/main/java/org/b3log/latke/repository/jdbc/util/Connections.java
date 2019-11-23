/*
 * Copyright (c) 2009-present, b3log.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.repository.jdbc.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Callstacks;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC connection utilities.
 * <p>
 * Uses <a href="https://github.com/brettwooldridge/HikariCP">HikariCP</a> as the underlying connection pool.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="https://hacpai.com/member/DASHU">DASHU</a>
 * @version 2.1.0.0, Nov 23, 2019
 */
public final class Connections {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Connections.class);

    /**
     * Connection pool - HikariCP.
     */
    private static HikariDataSource hikari;

    /**
     * Transaction isolation integer value.
     */
    private static int transactionIsolationInt;

    /**
     * JDBC URL.
     */
    private static String url;

    /**
     * JDBC user name.
     */
    private static String userName;

    /**
     * JDBC password.
     */
    private static String password;

    static {
        try {
            if (Latkes.RuntimeDatabase.NONE != Latkes.getRuntimeDatabase()) {
                final String driver = Latkes.getLocalProperty("jdbc.driver");
                Class.forName(driver);

                url = Latkes.getLocalProperty("jdbc.URL");
                userName = Latkes.getLocalProperty("jdbc.username");
                password = Latkes.getLocalProperty("jdbc.password");
                final int minConnCnt = Integer.valueOf(Latkes.getLocalProperty("jdbc.minConnCnt"));
                final int maxConnCnt = Integer.valueOf(Latkes.getLocalProperty("jdbc.maxConnCnt"));

                final String transactionIsolation = Latkes.getLocalProperty("jdbc.transactionIsolation");
                if (StringUtils.isBlank(transactionIsolation)) {
                    transactionIsolationInt = Connection.TRANSACTION_READ_COMMITTED;
                } else {
                    if ("NONE".equals(transactionIsolation)) {
                        transactionIsolationInt = Connection.TRANSACTION_NONE;
                    } else if ("READ_COMMITTED".equals(transactionIsolation)) {
                        transactionIsolationInt = Connection.TRANSACTION_READ_COMMITTED;
                    } else if ("READ_UNCOMMITTED".equals(transactionIsolation)) {
                        transactionIsolationInt = Connection.TRANSACTION_READ_UNCOMMITTED;
                    } else if ("REPEATABLE_READ".equals(transactionIsolation)) {
                        transactionIsolationInt = Connection.TRANSACTION_REPEATABLE_READ;
                    } else if ("SERIALIZABLE".equals(transactionIsolation)) {
                        transactionIsolationInt = Connection.TRANSACTION_SERIALIZABLE;
                    } else {
                        throw new IllegalStateException("Undefined transaction isolation [" + transactionIsolation + ']');
                    }
                }

                LOGGER.log(Level.DEBUG, "Initialing database connection pool [hikari]");
                final Properties props = new Properties();
                final InputStream is = Connections.class.getResourceAsStream("/hikari.properties");
                if (null != is) {
                    props.load(is);
                    final HikariConfig hikariConfig = new HikariConfig(props);
                    hikari = new HikariDataSource(hikariConfig);
                    LOGGER.log(Level.INFO, "Created datasource with hikari.properties");
                } else {
                    hikari = new HikariDataSource();
                    if (Latkes.RuntimeDatabase.ORACLE == Latkes.getRuntimeDatabase()) {
                        hikari.setConnectionTestQuery("SELECT 1 FROM DUAL");
                    } else {
                        hikari.setConnectionTestQuery("SELECT 1");
                    }

                    if (Latkes.RuntimeDatabase.MYSQL == Latkes.getRuntimeDatabase()) {
                        // 内置 HikariCP 对 MySQL 的优化配置 https://github.com/b3log/latke/issues/159
                        hikari.addDataSourceProperty("dataSource.cachePrepStmts", true);
                        hikari.addDataSourceProperty("dataSource.prepStmtCacheSize", 256);
                        hikari.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", 2048);
                        hikari.addDataSourceProperty("dataSource.useServerPrepStmts", true);
                        hikari.addDataSourceProperty("dataSource.useLocalSessionState", true);
                        hikari.addDataSourceProperty("dataSource.rewriteBatchedStatements", true);
                        hikari.addDataSourceProperty("dataSource.cacheResultSetMetadata", true);
                        hikari.addDataSourceProperty("dataSource.cacheServerConfiguration", true);
                        hikari.addDataSourceProperty("dataSource.elideSetAutoCommits", true);
                        hikari.addDataSourceProperty("dataSource.maintainTimeStats", false);
                    }
                    hikari.setValidationTimeout(2000);
                    hikari.setConnectionTimeout(2000);
                    hikari.setLeakDetectionThreshold(300000);
                    hikari.setUsername(userName);
                    hikari.setPassword(password);
                    hikari.setJdbcUrl(url);
                    hikari.setDriverClassName(driver);
                    hikari.setMinimumIdle(minConnCnt);
                    hikari.setMaximumPoolSize(maxConnCnt);
                }

                LOGGER.info("Initialized database connection pool [hikari]");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Can not initialize database connection pool", e);
        }
    }

    /**
     * Gets the total connection count.
     *
     * @return total connection count
     */
    public static int getTotalConnectionCount() {
        if (Latkes.RuntimeDatabase.NONE == Latkes.getRuntimeDatabase()) {
            return -1;
        }

        return hikari.getHikariPoolMXBean().getTotalConnections();
    }

    /**
     * Gets the max connection count.
     *
     * @return max connection count
     */
    public static int getMaxConnectionCount() {
        if (Latkes.RuntimeDatabase.NONE == Latkes.getRuntimeDatabase()) {
            return -1;
        }

        return hikari.getMaximumPoolSize();
    }

    /**
     * Gets the active connection count.
     *
     * @return active connection count
     */
    public static int getActiveConnectionCount() {
        if (Latkes.RuntimeDatabase.NONE == Latkes.getRuntimeDatabase()) {
            return -1;
        }

        return hikari.getHikariPoolMXBean().getActiveConnections();
    }

    /**
     * Gets a connection.
     *
     * @return a connection
     * @throws SQLException SQL exception
     */
    public static Connection getConnection() throws SQLException {
        if (LOGGER.isTraceEnabled()) {
            Callstacks.printCallstack(Level.TRACE, new String[]{"org.b3log"}, null);
        }

        if (Latkes.RuntimeDatabase.NONE == Latkes.getRuntimeDatabase()) {
            return null;
        }

        final Connection ret = hikari.getConnection();
        ret.setTransactionIsolation(transactionIsolationInt);
        ret.setAutoCommit(false);

        return ret;
    }

    /**
     * Shutdowns the connection pool.
     */
    public static void shutdownConnectionPool() {
        if (null == hikari) {
            return;
        }

        hikari.close();
        LOGGER.debug("Closed database connection pool");
    }

    /**
     * Checks whether the connection pool is closed.
     *
     * @return {@code true} if it is, returns {@code false} otherwise
     */
    public static boolean isClosed() {
        if (null == hikari) {
            return true;
        }

        return hikari.isClosed();
    }

    /**
     * Private constructor.
     */
    private Connections() {
    }
}
