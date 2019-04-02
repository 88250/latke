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
package org.b3log.latke.repository.oracle;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.jdbc.AbstractJdbcDatabaseSolution;
import org.b3log.latke.repository.jdbc.mapping.BooleanMapping;
import org.b3log.latke.repository.jdbc.mapping.IntMapping;
import org.b3log.latke.repository.jdbc.mapping.Mapping;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.FieldDefinition;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;
import org.b3log.latke.repository.oracle.mapping.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Oracle database solution.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Feb 21, 2019
 * @since 2.3.18
 */
public class OracleJdbcDatabaseSolution extends AbstractJdbcDatabaseSolution {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(OracleJdbcDatabaseSolution.class);

    /**
     * Public constructor.
     */
    public OracleJdbcDatabaseSolution() {
        registerType("int", new IntMapping());
        registerType("boolean", new BooleanMapping());
        registerType("long", new LongMapping());
        registerType("double", new NumberMapping());
        registerType("String", new StringMapping());
        registerType("Date", new DateMapping());
        registerType("Datetime", new DatetimeMapping());
    }

    @Override
    public boolean existTable(final String tableName) {
        try (final Connection connection = Connections.getConnection();
             final Statement statement = connection.createStatement()) {
            try {
                statement.execute("SELECT 1 FROM " + tableName + " ROWNUM <= 1");
            } catch (final Throwable e) {
                return false;
            }

            return true;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Checks table [" + tableName + "] existence failed, assumed it existing [true]", e);

            return true;
        }
    }

    @Override
    public String queryPage(final int start, final int end, final String selectSql, final String filterSql, final String orderBySql,
                            final String tableName) {
        /*
SELECT * FROM
(
    SELECT a.*, rownum r__
    FROM
    (
        SELECT * FROM ORDERS WHERE CustomerID LIKE 'A%'
        ORDER BY OrderDate DESC, ShippingDate DESC
    ) a
    WHERE rownum < ((pageNumber * pageSize) + 1 )
)
WHERE r__ >= (((pageNumber-1) * pageSize) + 1)
         */
        final StringBuilder sql = new StringBuilder();
        final String orderBy = StringUtils.isBlank(orderBySql) ? " order by " + JdbcRepositories.getDefaultKeyName() + " desc" : orderBySql;

        sql.append(selectSql).append(" from (select a.*, rownum r__ from (select * from ").append(tableName);
        if (StringUtils.isNotBlank(filterSql)) {
            sql.append(" where ").append(filterSql);
        }
        sql.append(orderBy).append(" ) a where rownum < ").append(end).append(") where r__ >= ").append(start);

        return sql.toString();
    }

    @Override
    public String getRandomlySql(final String tableName, final int fetchSize) {
/*
SELECT  *
FROM    (
        SELECT  *
        FROM    mytable
        ORDER BY
                dbms_random.value
        )
WHERE rownum <= 1000
 */
        final StringBuilder sql = new StringBuilder("SELECT ").append(" * FROM (").append("SELECT * FROM ").
                append(tableName).append(" ORDER BY dbms_random.value) WHERE rownum <=").append(fetchSize);

        return sql.toString();
    }

    @Override
    protected void createTableHead(final StringBuilder createTableSql, final RepositoryDefinition repositoryDefinition) {
        createTableSql.append("CREATE TABLE ").append(repositoryDefinition.getName()).append("(");
    }

    @Override
    protected void createTableBody(final StringBuilder createTableSql, final RepositoryDefinition repositoryDefinition) {
        final List<FieldDefinition> keyDefinitionList = new ArrayList<>();
        final List<FieldDefinition> fieldDefinitions = repositoryDefinition.getKeys();
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            final String type = fieldDefinition.getType();
            if (type == null) {
                throw new RuntimeException("The type of fieldDefinitions should not be null");
            }

            final Mapping mapping = getJdbcTypeMapping().get(type);
            if (mapping != null) {
                createTableSql.append(mapping.toDataBaseSting(fieldDefinition)).append(",   ");

                if (fieldDefinition.getIsKey()) {
                    keyDefinitionList.add(fieldDefinition);
                }
            } else {
                throw new RuntimeException("The type [" + fieldDefinition.getType() + "] is not register for mapping ");
            }
        }

        keyDefinitionList.size();
        createTableSql.append(createKeyDefinition(keyDefinitionList));
    }

    /**
     * the keyDefinitionList tableSql.
     *
     * @param keyDefinitionList keyDefinitionList
     * @return createKeyDefinitionsql
     */
    private String createKeyDefinition(final List<FieldDefinition> keyDefinitionList) {
        final StringBuilder sql = new StringBuilder();

        sql.append(" PRIMARY KEY");
        boolean isFirst = true;

        for (FieldDefinition fieldDefinition : keyDefinitionList) {
            if (isFirst) {
                sql.append("(");
                isFirst = false;
            } else {
                sql.append(",");
            }

            sql.append(fieldDefinition.getName());
        }

        sql.append(")");
        return sql.toString();
    }

    @Override
    protected void createTableEnd(final StringBuilder createTableSql, final RepositoryDefinition repositoryDefinition) {
        createTableSql.append(")");
    }
}
