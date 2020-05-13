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
 * IntMapping.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.0, Jan 12, 2012
 */
public class IntMapping implements Mapping {

    @Override
    public String toDataBaseSting(final FieldDefinition definition) {
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(definition.getName());
        sqlBuilder.append(" INT");
        if (!definition.getNullable()) {
            sqlBuilder.append(" NOT NULL");
        }
        return sqlBuilder.toString();
    }
}
