/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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

import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;

import java.sql.SQLException;

/**
 * interface JdbcDatabase.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Mar 15, 2018
 */
public interface JdbcDatabase {

    /**
     * Creates table with the specified repository definition.
     *
     * @param repositoryDefinition the specified repository definition
     * @return {@code true} if successfully, returns {@code false} otherwise
     * @throws SQLException SQLException
     */
    boolean createTable(final RepositoryDefinition repositoryDefinition) throws SQLException;

    /**
     * @param tableName tableName
     * @param ifdrop    ifdrop
     *                  <P>
     *                  ifdrop true: using drop
     *                  not: using truncate to clear data.
     *                  </p>
     * @return if success to clearTable
     * @throws SQLException SQLException
     */
    boolean clearTable(final String tableName, final boolean ifdrop) throws SQLException;

    /**
     * queryPage sql.
     *
     * @param start      start
     * @param end        end
     * @param selectSql  selectSql
     * @param filterSql  filterSql
     * @param orderBySql orderBySql
     * @param tableName  tableName
     * @return sql
     */
    String queryPage(final int start, final int end, final String selectSql, final String filterSql, final String orderBySql, final String tableName);

    /**
     * getRandomlySql.
     *
     * @param tableName tableName
     * @param fetchSize fetchSize
     * @return sql sql
     */
    String getRandomlySql(final String tableName, final int fetchSize);
}
