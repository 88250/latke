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
package org.b3log.latke.repository.sqlserver;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.repository.jdbc.AbstractJdbcDatabaseSolution;
import org.b3log.latke.repository.jdbc.mapping.BooleanMapping;
import org.b3log.latke.repository.jdbc.mapping.IntMapping;
import org.b3log.latke.repository.jdbc.mapping.LongMapping;
import org.b3log.latke.repository.jdbc.mapping.Mapping;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.FieldDefinition;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;
import org.b3log.latke.repository.sqlserver.mapping.DateMapping;
import org.b3log.latke.repository.sqlserver.mapping.DatetimeMapping;
import org.b3log.latke.repository.sqlserver.mapping.DecimalMapping;
import org.b3log.latke.repository.sqlserver.mapping.StringMapping;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Microsoft SQL Server database solution.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.2, May 13, 2020
 * @since 1.0.8
 */
public class SQLServerJdbcDatabaseSolution extends AbstractJdbcDatabaseSolution {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(SQLServerJdbcDatabaseSolution.class);

    /**
     * Public constructor.
     */
    public SQLServerJdbcDatabaseSolution() {
        registerType("int", new IntMapping());
        registerType("boolean", new BooleanMapping());
        registerType("long", new LongMapping());
        registerType("Decimal", new DecimalMapping());
        registerType("String", new StringMapping());
        registerType("Date", new DateMapping());
        registerType("Datetime", new DatetimeMapping());
    }

    @Override
    public boolean existTable(final String tableName) {
        try (final Connection connection = Connections.getConnection();
             final Statement statement = connection.createStatement()) {
            try {
                statement.execute("SELECT TOP 1 * FROM " + tableName);
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
        final StringBuilder sqlBuilder = new StringBuilder();

        /*
         select * from 
         (
         select ROW_NUMBER() over(order by id desc) rownum, *  
         from .... where .... order by ....
         ) a 
         where rownum>10000 and rownum<10501
         */
        final String over = StringUtils.isBlank(orderBySql) ? "order by " + JdbcRepositories.keyName + " desc" : orderBySql;
        sqlBuilder.append(selectSql).append(" from (select top 100 percent ROW_NUMBER() over(").append(over).append(") rownum, * from ").append(
                tableName);
        if (StringUtils.isNotBlank(filterSql)) {
            sqlBuilder.append(" where ").append(filterSql);
        }
        sqlBuilder.append(orderBySql);
        sqlBuilder.append(" ) a where rownum > ").append(start).append(" and rownum <= ").append(end);
        return sqlBuilder.toString();
    }

    @Override
    public String getRandomlySql(final String tableName, final int fetchSize) {
        /*
         SELECT TOP 5 *
         FROM Test.dbo.basetable
         ORDER BY CHECKSUM(NEWID())
         */
        final StringBuilder sqlBuilder = new StringBuilder("SELECT TOP ").append(fetchSize).append(" * FROM ").
                append(tableName).append(" ORDER BY CHECKSUM(NEWID())");
        return sqlBuilder.toString();
    }

    @Override
    protected void createTableHead(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition) {
        /*
         IF NOT EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[tablename]')
         AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
         CREATE TABLE [dbo].[tablename] ( columns specification );
         */
        createTableSqlBuilder.append("IF NOT EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[").append(
                repositoryDefinition.getName()).append("]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1) ");
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
                createTableSqlBuilder.append(mapping.toDataBaseSting(fieldDefinition)).append(",   ");
                if (fieldDefinition.getIsKey()) {
                    keyDefinitionList.add(fieldDefinition);
                }
            } else {
                throw new RuntimeException("The type [" + fieldDefinition.getType() + "] is not register for mapping ");
            }

        }

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
        createTableSqlBuilder.append(");");
    }
}
