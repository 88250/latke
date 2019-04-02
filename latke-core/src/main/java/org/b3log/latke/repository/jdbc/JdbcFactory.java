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
package org.b3log.latke.repository.jdbc;

import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC Factory.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Feb 21, 2019
 */
public final class JdbcFactory implements JdbcDatabase {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JdbcRepository.class);

    /**
     * the holder of the databaseSolution.
     */
    private AbstractJdbcDatabaseSolution databaseSolution;

    /**
     * the singleton of jdbcfactory.
     */
    private static JdbcFactory jdbcFactory;

    /**
     * All JdbcDatabaseSolution class names.
     */
    private static Map<Latkes.RuntimeDatabase, String> jdbcDatabaseSolutionMap = new HashMap<Latkes.RuntimeDatabase, String>() {
        {
            put(Latkes.RuntimeDatabase.MYSQL, "org.b3log.latke.repository.mysql.MysqlJdbcDatabaseSolution");
            put(Latkes.RuntimeDatabase.H2, "org.b3log.latke.repository.h2.H2JdbcDatabaseSolution");
            put(Latkes.RuntimeDatabase.MSSQL, "org.b3log.latke.repository.sqlserver.SQLServerJdbcDatabaseSolution");
            put(Latkes.RuntimeDatabase.ORACLE, "org.b3log.latke.repository.oracle.OracleJdbcDatabaseSolution");
        }
    };

    @Override
    public boolean existTable(final String tableName) {
        return databaseSolution.existTable(tableName);
    }

    @Override
    public boolean createTable(final RepositoryDefinition repositoryDefinition) throws SQLException {
        return databaseSolution.createTable(repositoryDefinition);
    }

    /**
     * Gets the singleton instance of JdbcFactory.
     *
     * @return JdbcFactory singleton instance
     */
    public static synchronized JdbcFactory getInstance() {
        if (null == jdbcFactory) {
            jdbcFactory = new JdbcFactory();
        }

        return jdbcFactory;
    }

    /**
     * Private constructor.
     */
    private JdbcFactory() {

        /**
         * Latkes.getRuntimeDatabase(); 
         */
        final String databaseSolutionClassName = jdbcDatabaseSolutionMap.get(Latkes.getRuntimeDatabase());

        try {
            databaseSolution = (AbstractJdbcDatabaseSolution) Class.forName(databaseSolutionClassName).newInstance();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Init JdbcDatabaseSolution [" + databaseSolutionClassName + "] instance failed", e);
        }
    }

    @Override
    public String queryPage(final int start, final int end, final String selectSql, final String filterSql, final String orderBySql, final String tableName) {
        return databaseSolution.queryPage(start, end, selectSql, filterSql, orderBySql, tableName);
    }

    @Override
    public String getRandomlySql(final String tableName, final int fetchSize) {
        return databaseSolution.getRandomlySql(tableName, fetchSize);
    }
}
