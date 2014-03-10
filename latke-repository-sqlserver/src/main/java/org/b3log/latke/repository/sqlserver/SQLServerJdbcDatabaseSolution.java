/*
 * Copyright (c) 2014, B3log Team
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
package org.b3log.latke.repository.sqlserver;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.repository.jdbc.AbstractJdbcDatabaseSolution;
import org.b3log.latke.repository.jdbc.mapping.BooleanMapping;
import org.b3log.latke.repository.jdbc.mapping.IntMapping;
import org.b3log.latke.repository.jdbc.mapping.LongMapping;
import org.b3log.latke.repository.jdbc.mapping.Mapping;
import org.b3log.latke.repository.jdbc.mapping.StringMapping;
import org.b3log.latke.repository.jdbc.util.FieldDefinition;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.sqlserver.mapping.DateMapping;
import org.b3log.latke.repository.sqlserver.mapping.DatetimeMapping;
import org.b3log.latke.repository.sqlserver.mapping.DecimalMapping;
import org.b3log.latke.util.Strings;


/**
 * Microsoft SQL Server database solution.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Mar 10, 2014
 * @since 1.0.8
 */
public class SQLServerJdbcDatabaseSolution extends AbstractJdbcDatabaseSolution {

    /**
     * Public constructor.
     */
    public SQLServerJdbcDatabaseSolution() {
        registerType("int", new IntMapping());
        registerType("boolean", new BooleanMapping());
        registerType("long", new LongMapping());
        registerType("decimal", new DecimalMapping());
        registerType("String", new StringMapping());
        registerType("Date", new DateMapping());
        registerType("Datetime", new DatetimeMapping());
    }

    @Override
    public String queryPage(final int start, final int end, final String selectSql, final String filterSql, final String orderBySql,
        final String tableName) {
        final StringBuilder sql = new StringBuilder();

        /*
         select * from 
         (
         select ROW_NUMBER() over(order by id desc) rownum, *  
         from .... where .... order by ....
         ) a 
         where rownum>10000 and rownum<10501
         */
        final String over = Strings.isEmptyOrNull(orderBySql) ? "order by " + JdbcRepositories.getDefaultKeyName() + " desc" : orderBySql;

        sql.append(selectSql).append(" from (select top 100 percent ROW_NUMBER() over(").append(over).append(") rownum, * from ").append(
            tableName);
        if (StringUtils.isNotBlank(filterSql)) {
            sql.append(" where ").append(filterSql);
        }

        sql.append(orderBySql);
        sql.append(" ) a where rownum > ").append(start).append(" and rownum <= ").append(end);

        return sql.toString();
    }

    @Override
    public String getRandomlySql(final String tableName, final int fetchSize) {

        /*
         SELECT TOP 5 *
         FROM Test.dbo.basetable
         ORDER BY CHECKSUM(NEWID())
         */
        final StringBuilder sql = new StringBuilder("SELECT TOP ").append(fetchSize).append(" * FROM ").append(tableName).append(
            " ORDER BY CHECKSUM(NEWID())");

        return sql.toString();
    }

    @Override
    protected void createDropTableSql(final StringBuilder dropTableSql, final String tableName) {

        /*
         IF EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[tablename]')
         AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
         DROP TABLE [dbo].[tablename]; 
         */
        dropTableSql.append("IF EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[").append(tableName).append("]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1) DROP TABLE [dbo].[").append(tableName).append(
            "];");
    }

    @Override
    protected void createTableHead(final StringBuilder createTableSql, final String tableName) {

        /*
         IF NOT EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[tablename]')
         AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
         CREATE TABLE [dbo].[tablename] ( columns specification );
         */
        createTableSql.append("IF NOT EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[").append(tableName).append(
            "]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1) ");
    }

    @Override
    protected void createTableBody(final StringBuilder createTableSql, final List<FieldDefinition> fieldDefinitions) {
        final List<FieldDefinition> keyDefinitionList = new ArrayList<FieldDefinition>();

        for (FieldDefinition fieldDefinition : fieldDefinitions) {

            final String type = fieldDefinition.getType();

            if (type == null) {
                throw new RuntimeException("the type of fieldDefinitions should not be null");
            }
            final Mapping mapping = getJdbcTypeMapping().get(type);

            if (mapping != null) {
                createTableSql.append(mapping.toDataBaseSting(fieldDefinition)).append(",   ");

                if (fieldDefinition.getIsKey()) {
                    keyDefinitionList.add(fieldDefinition);
                }
            } else {
                throw new RuntimeException("the type[" + fieldDefinition.getType() + "] is not register for mapping ");
            }

        }

        if (keyDefinitionList.size() < 0) {
            throw new RuntimeException("no key talbe is not allow");
        } else {
            createTableSql.append(createKeyDefinition(keyDefinitionList));
        }

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
    protected void createTableEnd(final StringBuilder createTableSql) {
        createTableSql.append(");");
    }

    @Override
    public void clearTableSql(final StringBuilder clearTableSq, final String tableName, final boolean ifdrop) {
        if (ifdrop) {
            clearTableSq.append("DROP TABLE IF EXISTS ").append(tableName);
        } else {
            clearTableSq.append("TRUNCATE TABLE ").append(tableName);
        }
    }
}
