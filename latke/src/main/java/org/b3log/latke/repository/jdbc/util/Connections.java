/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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


import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeDatabase;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Callstacks;
import org.h2.jdbcx.JdbcConnectionPool;


/**
 * JDBC connection utilities.
 *
 * <p>
 * Uses <a href="http://jolbox.com/">BoneCP</a>,
 * <a href="http://sourceforge.net/projects/c3p0/">c3p0</a> or
 * <a href="http://www.h2database.com">H2</a> as the underlying connection pool.
 * </p>
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:385321165@qq.com">DASHU</a>
 * @version 1.0.1.2, Feb 7, 2014
 */
public final class Connections {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Connections.class.getName());

    /**
     * Pool type.
     */
    private static String poolType;

    /**
     * Get connection timeout.
     */
    private static final long CONN_TIMEOUT = 5000;

    /**
     * Connection pool - BoneCP.
     */
    private static BoneCP boneCP;

    /**
     * Connection pool - c3p0.
     */
    private static ComboPooledDataSource c3p0;

    /**
     * C3p0 pool check time.
     */
    private static final int C3P0_CHECKTIME = 60;

    /**
     * Connection pool - H2.
     */
    private static JdbcConnectionPool h2;

    /**
     * Transaction isolation.
     */
    private static String transactionIsolation;

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
            if (RuntimeDatabase.NONE != Latkes.getRuntimeDatabase()) {
                final String driver = Latkes.getLocalProperty("jdbc.driver");

                Class.forName(driver);

                poolType = Latkes.getLocalProperty("jdbc.pool");

                url = Latkes.getLocalProperty("jdbc.URL");
                userName = Latkes.getLocalProperty("jdbc.username");
                password = Latkes.getLocalProperty("jdbc.password");
                final int minConnCnt = Integer.valueOf(Latkes.getLocalProperty("jdbc.minConnCnt"));
                final int maxConnCnt = Integer.valueOf(Latkes.getLocalProperty("jdbc.maxConnCnt"));

                transactionIsolation = Latkes.getLocalProperty("jdbc.transactionIsolation");
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

                if ("BoneCP".equals(poolType)) {
                    LOGGER.log(Level.DEBUG, "Initializing database connection pool [BoneCP]");

                    final BoneCPConfig config = new BoneCPConfig();

                    config.setDefaultAutoCommit(false);
                    config.setDefaultTransactionIsolation(transactionIsolation);
                    config.setJdbcUrl(url);
                    config.setUsername(userName);
                    config.setPassword(password);
                    config.setMinConnectionsPerPartition(minConnCnt);
                    config.setMaxConnectionsPerPartition(maxConnCnt);
                    config.setPartitionCount(1);
                    config.setDisableJMX(true);
                    config.setConnectionTimeoutInMs(CONN_TIMEOUT);

                    boneCP = new BoneCP(config);
                } else if ("c3p0".equals(poolType)) {
                    LOGGER.log(Level.DEBUG, "Initializing database connection pool [c3p0]");

                    // Disable JMX
                    System.setProperty("com.mchange.v2.c3p0.management.ManagementCoordinator",
                        "com.mchange.v2.c3p0.management.NullManagementCoordinator");

                    c3p0 = new ComboPooledDataSource();
                    c3p0.setUser(userName);
                    c3p0.setPassword(password);
                    c3p0.setJdbcUrl(url);
                    c3p0.setDriverClass(driver);
                    c3p0.setInitialPoolSize(minConnCnt);
                    c3p0.setMinPoolSize(minConnCnt);
                    c3p0.setMaxPoolSize(maxConnCnt);
                    c3p0.setMaxStatementsPerConnection(maxConnCnt);
                    c3p0.setTestConnectionOnCheckin(true);
                    c3p0.setCheckoutTimeout(maxConnCnt);
                    c3p0.setIdleConnectionTestPeriod(C3P0_CHECKTIME);
                    c3p0.setCheckoutTimeout((int) CONN_TIMEOUT);
                } else if ("h2".equals(poolType)) {
                    LOGGER.log(Level.DEBUG, "Initialing database connection pool [h2]");

                    h2 = JdbcConnectionPool.create(url, userName, password);
                    h2.setMaxConnections(maxConnCnt);
                } else if ("none".equals(poolType)) {
                    LOGGER.info("Do not use database connection pool");
                }

                LOGGER.info("Initialized connection pool [type=" + poolType + ']');
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Can not initialize database connection", e);
        }
    }

    /**
     * Gets a connection.
     *
     * @return a connection
     * @throws SQLException SQL exception
     */
    public static Connection getConnection() throws SQLException {
        if (LOGGER.isTraceEnabled()) {
            Callstacks.printCallstack(Level.TRACE, new String[] {"org.b3log"}, null);
        }

        if ("BoneCP".equals(poolType)) {
            LOGGER.log(Level.TRACE, "Connection pool[createdConns={0}, freeConns={1}, leasedConns={2}]",
                new Object[] {boneCP.getTotalCreatedConnections(), boneCP.getTotalFree(), boneCP.getTotalLeased()});

            return boneCP.getConnection();
        } else if ("c3p0".equals(poolType)) {
            LOGGER.log(Level.TRACE, "Connection pool[createdConns={0}, freeConns={1}, leasedConns={2}]",
                new Object[] {c3p0.getNumConnections(), c3p0.getNumIdleConnections(), c3p0.getNumBusyConnections()});
            final Connection ret = c3p0.getConnection();

            ret.setTransactionIsolation(transactionIsolationInt);
            ret.setAutoCommit(false);

            return ret;
        } else if ("h2".equals(poolType)) {
            LOGGER.log(Level.TRACE, "Connection pool[leasedConns={0}]", new Object[] {h2.getActiveConnections()});
            final Connection ret = h2.getConnection();

            ret.setTransactionIsolation(transactionIsolationInt);
            ret.setAutoCommit(false);

            return ret;
        } else if ("none".equals(poolType)) {
            final Connection ret = DriverManager.getConnection(url, userName, password);

            ret.setTransactionIsolation(transactionIsolationInt);
            ret.setAutoCommit(false);

            return ret;
        } else if (RuntimeDatabase.NONE == Latkes.getRuntimeDatabase()) {
            return null;
        }

        throw new IllegalStateException("Not found database connection pool [" + poolType + "]");
    }

    /**
     * Shutdowns the connection pool.
     */
    public static void shutdownConnectionPool() {
        if (null != boneCP) {
            boneCP.shutdown();
            LOGGER.info("Closed [BoneCP] database connection pool");
        }

        if (null != h2) {
            h2.dispose();
            LOGGER.info("Closed [H2] database connection pool");
        }

        if (null != c3p0) {
            c3p0.close();
            LOGGER.info("Closed [c3p0] database connection pool");
        }
    }

    /**
     * Private constructor.
     */
    private Connections() {}
}
