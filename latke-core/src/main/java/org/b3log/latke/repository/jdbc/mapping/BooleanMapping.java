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
package org.b3log.latke.repository.jdbc.mapping;


import org.b3log.latke.repository.jdbc.util.FieldDefinition;


/**
 * Boolean data type mapping.
 *
 * <p>
 * The data type is CHAR(1), we could INSERT INTO `test`(`test`) VALUES (false/true) in MySQL database, the actual value is '0' for false,
 * '1' for true.
 * </p>
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.1, Dec 27, 2012
 */
public class BooleanMapping implements Mapping {

    @Override
    public String toDataBaseSting(final FieldDefinition definition) {
        final StringBuilder sql = new StringBuilder();

        sql.append(definition.getName());
        sql.append(" char(1)");
        if (!definition.getNullable()) {
            sql.append(" not null");

        }

        return sql.toString();
    }
}
