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
package org.b3log.latke.repository.jdbc;

import java.sql.SQLException;
import java.util.List;

import org.b3log.latke.repository.jdbc.util.FieldDefinition;

/**
 * interface JdbcDatabase.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Dec 20, 2011
 */
public interface JdbcDatabase {

    /**
     * createTable.
     * @param tableName tableName
     * @param fieldDefinitions fieldDefinitions
     * 
     * @return ifseccuss
     * @throws SQLException SQLException 
     */
    boolean createTable(String tableName, List<FieldDefinition> fieldDefinitions)
            throws SQLException;

    /**
     * 
     * @param tableName tableName
     * @param ifdrop ifdrop 
     * <P>
     *  ifdrop true: using drop
     *         not: using truncate to clear data.
     * </p>
     * @throws SQLException   SQLException
     * @return if success to clearTable
     */
    boolean clearTable(final String tableName, final boolean ifdrop) throws SQLException;

    /**
     * queryPage sql.
     * 
     * @param start start
     * @param end end
     * @param selectSql selectSql
     * @param filterSql filterSql
     * @param orderBySql orderBySql
     * @param tableName tableName
     * @return sql 
     */
    String queryPage(int start, int end, String selectSql, String filterSql,
            String orderBySql, String tableName);

    /**
     * getRandomlySql.
     * 
     * @param tableName tableName
     * @param fetchSize fetchSize
     * @return sql sql
     */
    String getRandomlySql(final String tableName, int fetchSize);

}
