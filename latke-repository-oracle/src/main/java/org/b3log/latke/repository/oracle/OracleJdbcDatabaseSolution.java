/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.repository.oracle;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * @version 2.0.0.2, May 13, 2020
 * @since 2.3.18
 */
public class OracleJdbcDatabaseSolution extends AbstractJdbcDatabaseSolution {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(OracleJdbcDatabaseSolution.class);

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
    public String queryPage(final int start, final int end, final String selectSql, final String filterSql, final String orderBySql, final String tableName) {
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
        final StringBuilder sqlBuilder = new StringBuilder();
        final String orderBy = StringUtils.isBlank(orderBySql) ? " order by " + JdbcRepositories.keyName + " desc" : orderBySql;
        sqlBuilder.append(selectSql).append(" from (select a.*, rownum r__ from (select * from ").append(tableName);
        if (StringUtils.isNotBlank(filterSql)) {
            sqlBuilder.append(" where ").append(filterSql);
        }
        sqlBuilder.append(orderBy).append(" ) a where rownum < ").append(end).append(") where r__ >= ").append(start);
        return sqlBuilder.toString();
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
        return "SELECT " + " * FROM (" + "SELECT * FROM " + tableName + " ORDER BY dbms_random.value) WHERE rownum <=" + fetchSize;
    }

    @Override
    protected void createTableHead(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition) {
        createTableSqlBuilder.append("CREATE TABLE ").append(repositoryDefinition.getName()).append("(");
    }

    @Override
    protected void createTableBody(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition) {
        final List<FieldDefinition> keyDefinitionList = new ArrayList<>();
        final List<FieldDefinition> fieldDefinitions = repositoryDefinition.getKeys();
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            final String type = fieldDefinition.getType();
            if (type == null) {
                throw new RuntimeException("The type of fieldDefinitions should not be null");
            }

            final Mapping mapping = getJdbcTypeMapping().get(type);
            if (mapping != null) {
                createTableSqlBuilder.append(mapping.toDataBaseSting(fieldDefinition)).append(", ");
                if (fieldDefinition.getIsKey()) {
                    keyDefinitionList.add(fieldDefinition);
                }
            } else {
                throw new RuntimeException("The type [" + fieldDefinition.getType() + "] is not register for mapping ");
            }
        }

        createSoftDeleteField(createTableSqlBuilder);
        createTableSqlBuilder.append(createKeyDefinition(keyDefinitionList));
    }

    /**
     * the keyDefinitionList tableSql.
     *
     * @param keyDefinitionList keyDefinitionList
     * @return createKeyDefinitionsql
     */
    private String createKeyDefinition(final List<FieldDefinition> keyDefinitionList) {
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" PRIMARY KEY");
        boolean isFirst = true;
        for (FieldDefinition fieldDefinition : keyDefinitionList) {
            if (isFirst) {
                sqlBuilder.append("(");
                isFirst = false;
            } else {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(fieldDefinition.getName());
        }
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    @Override
    protected void createTableEnd(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition) {
        createTableSqlBuilder.append(")");
    }
}
