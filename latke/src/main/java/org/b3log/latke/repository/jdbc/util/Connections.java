/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Callstacks;
import org.h2.jdbcx.JdbcConnectionPool;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC connection utilities.
 * <p>
 * Uses <a href="https://github.com/alibaba/druid">Druid</a> or <a href="http://www.h2database.com">H2</a> as the underlying connection pool.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:385321165@qq.com">DASHU</a>
 * @version 1.2.3.4, Oct 16, 2017
 */
public final class Connections {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Connections.class);

    /**
     * Pool type.
     */
    private static String poolType;

    /**
     * Get connection timeout.
     */
    private static final int CONN_TIMEOUT = 5000;

    /**
     * Connection pool - H2.
     */
    private static JdbcConnectionPool h2;

    /**
     * Connection pool - Druid.
     */
    private static DruidDataSource druid;

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
            if (Latkes.RuntimeDatabase.NONE != Latkes.getRuntimeDatabase()) {
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

                if ("h2".equals(poolType)) {
                    LOGGER.log(Level.DEBUG, "Initialing database connection pool [h2]");

                    h2 = JdbcConnectionPool.create(url, userName, password);
                    h2.setMaxConnections(maxConnCnt);
                } else if ("druid".equals(poolType)) {
                    LOGGER.log(Level.DEBUG, "Initialing database connection pool [druid]");

                    final Properties props = new Properties();
                    final InputStream is = Connections.class.getResourceAsStream("/druid.properties");
                    if (null != is) {
                        props.load(is);
                        druid = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
                    } else {
                        druid = new DruidDataSource();
                        druid.setTestOnReturn(true);
                        druid.setTestOnBorrow(false);
                        druid.setTestWhileIdle(true);
                        if (Latkes.RuntimeDatabase.ORACLE == Latkes.getRuntimeDatabase()) {
                            druid.setValidationQuery("SELECT 1 FROM DUAL");
                        } else {
                            druid.setValidationQuery("SELECT 1");
                        }
                        druid.setMaxWait(CONN_TIMEOUT);
                        druid.setValidationQueryTimeout(CONN_TIMEOUT);
                    }

                    druid.setUsername(userName);
                    druid.setPassword(password);
                    druid.setUrl(url);
                    druid.setDriverClassName(driver);
                    druid.setInitialSize(minConnCnt);
                    druid.setMinIdle(minConnCnt);
                    druid.setMaxActive(maxConnCnt);
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
            Callstacks.printCallstack(Level.TRACE, new String[]{"org.b3log"}, null);
        }

        if ("h2".equals(poolType)) {
            LOGGER.log(Level.TRACE, "Connection pool[leasedConns={0}]", new Object[]{h2.getActiveConnections()});
            final Connection ret = h2.getConnection();

            ret.setTransactionIsolation(transactionIsolationInt);
            ret.setAutoCommit(false);

            return ret;
        } else if ("druid".equals(poolType)) {
            LOGGER.log(Level.TRACE, "Connection pool[leasedConns={0}]", new Object[]{druid.getActiveConnections()});

            final Connection ret = druid.getConnection();

            ret.setTransactionIsolation(transactionIsolationInt);
            ret.setAutoCommit(false);

            return ret;
        } else if ("none".equals(poolType)) {
            final Connection ret = DriverManager.getConnection(url, userName, password);

            ret.setTransactionIsolation(transactionIsolationInt);
            ret.setAutoCommit(false);

            return ret;
        } else if (Latkes.RuntimeDatabase.NONE == Latkes.getRuntimeDatabase()) {
            return null;
        }

        throw new IllegalStateException("Not found database connection pool [" + poolType + "]");
    }

    /**
     * Shutdowns the connection pool.
     */
    public static void shutdownConnectionPool() {
        if (null != h2) {
            h2.dispose();
            LOGGER.info("Closed [H2] database connection pool");
        }

        if (null != druid) {
            druid.close();
            LOGGER.info("Closed [druid] database connection pool");
        }
    }

    /**
     * Private constructor.
     */
    private Connections() {
    }
}
