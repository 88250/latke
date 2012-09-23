/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.util.Callstacks;

/**
 * JDBC connection utilities.
 *
 * <p>
 * Uses <a href="http://jolbox.com/">BoneCP</a> or 
 * <a href="http://sourceforge.net/projects/c3p0/">c3p0</a> as the underlying connection pool.
 * </p>
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Sep 4, 2012
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
     * Connection pool - BoneCP.
     */
    private static BoneCP boneCP;
    /**
     * Connection pool - c3p0.
     */
    private static ComboPooledDataSource c3p0;
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
            final String driver = Latkes.getLocalProperty("jdbc.driver");
            Class.forName(driver);

            if (RuntimeEnv.BAE == Latkes.getRuntimeEnv()) {
                poolType = "none";
            } else {
                poolType = Latkes.getLocalProperty("jdbc.pool");
            }

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
                LOGGER.log(Level.FINE, "Initializing database connection pool [BoneCP]");

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

                boneCP = new BoneCP(config);
            } else if ("c3p0".equals(poolType)) {
                LOGGER.log(Level.FINE, "Initializing database connection pool [c3p0]");

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
            } else if ("none".equals(poolType)) {
                LOGGER.info("Do not use database connection pool");
            }

            LOGGER.info("Initialized connection pool [type=" + poolType + ']');
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Can not initialize database connection", e);
        }
    }

    /**
     * Gets a connection.
     *
     * @return a connection
     * @throws SQLException SQL exception
     */
    public static Connection getConnection() throws SQLException {
        if (LOGGER.isLoggable(Level.FINEST)) {
            Callstacks.printCallstack(Level.FINEST, new String[]{"org.b3log"}, null);
        }

        if ("BoneCP".equals(poolType)) {
            LOGGER.log(Level.FINEST, "Connection pool[createdConns={0}, freeConns={1}, leasedConns={2}]",
                    new Object[]{boneCP.getTotalCreatedConnections(), boneCP.getTotalFree(), boneCP.getTotalLeased()});

            return boneCP.getConnection();
        } else if ("c3p0".equals(poolType)) {
            LOGGER.log(Level.FINEST, "Connection pool[createdConns={0}, freeConns={1}, leasedConns={2}]",
                    new Object[]{c3p0.getNumConnections(), c3p0.getNumIdleConnections(), c3p0.getNumBusyConnections()});
            final Connection ret = c3p0.getConnection();
            ret.setTransactionIsolation(transactionIsolationInt);

            return ret;
        } else if ("none".equals(poolType)) {
            return DriverManager.getConnection(url, userName, password);
        }

        throw new IllegalStateException("Not found database connection pool [" + poolType + "]");
    }

    /**
     * Shutdowns the connection pool.
     */
    public static void shutdownConnectionPool() {
        if (null != boneCP) {
            boneCP.shutdown();
        }

        LOGGER.info("Shutdowns connection pool sucessfully");
    }

    /**
     * Private constructor.
     */
    private Connections() {
    }
}
